 /*
 ========================================================================================
 Name        : universAAL_continua_HDP_manager.c
 Author      : amartinez@tsbtecnologias.es & lgigante@tsbtecnologias.es
 Version     : 1.0
 Copyright   : TSB
 Description : Library to start working with dbus,bluetooth health profile and Java
 ========================================================================================
 */

/*****************************************************************************************************************************
 * Includes ******************************************************************************************************************
 ****************************************************************************************************************************/
#include <stdio.h>
#include <stdbool.h>
#include <stdarg.h>
#include <string.h>
#include <stdlib.h>
#include <errno.h>
#include <time.h>
#include <sys/fcntl.h>
#include <sys/time.h>
#include <sys/types.h>
#include <sys/signal.h>
#include <sys/socket.h>
#include <unistd.h>
#include "glib.h"
#include <dbus/dbus.h>
#include <jni.h>
#include "universAAL_continua_HDP_manager.h"

/*****************************************************************************************************************************
 * Defines *******************************************************************************************************************
 ****************************************************************************************************************************/
// Service name on the message bus
#define	SERVICE_NAME "org.bluez"
// Interfaces to call the functions
#define	MANAGER_INTERFACE_NAME "org.bluez.Manager"
#define	HEALTH_MANAGER_INTERFACE_NAME "org.bluez.HealthManager"
#define	HEALTH_DEVICE_INTERFACE_NAME "org.bluez.HealthDevice"
#define	HEALTH_CHANNEL_INTERFACE_NAME "org.bluez.HealthChannel"
#define	ADAPTER_INTERFACE_NAME "org.bluez.Adapter"
#define	DEVICE_INTERFACE_NAME "org.bluez.Device"
// Object inside the service
#define	MANAGER_OBJECT_PATH "/"
#define	HEALTH_MANAGER_OBJECT_PATH "/org/bluez"
// Max number of input arguments for dbus API functions
#define MAX_DATA_TYPE_ARGS 3
// Buffer for input data frames
#define MAX_TAM_INPUT_BUFFER 1024

/*****************************************************************************************************************************
 * Global variables***********************************************************************************************************
 ****************************************************************************************************************************/
// Remote application connection
static DBusConnection *dbusConnection = NULL;
// Message to be sent or received over dbus connections
static DBusMessage *dbusMessageSent, *dbusMessageReceived;
// Errors or exceptions
static DBusError dbusErrors;
// Message to iterate
static DBusMessageIter messageIterator;
// Reply message
static DBusPendingCall *pendingMessage;
// Time in ms waiting for a reply
const int timeOut = 5000;
// Default bluetooth adapter path
char *defaultAdapterPath;
// Remote device object path
char *remoteDevicePath;
// HDP application object path
char *hdpApplicationPath;
// HDP channel object path
char *hdpChannelPath;
// Data type to send in a output message (0 = invalid type)
static int dataTypeArgs[MAX_DATA_TYPE_ARGS] = {DBUS_TYPE_INVALID,DBUS_TYPE_INVALID,DBUS_TYPE_INVALID};
static int variantDataTypeArg = DBUS_TYPE_INVALID;
// Returned values (temp)
char *returnedStringValueTemp;
char *returnedObjectPathValueTemp;
// File descriptor
static unsigned int fileDescriptor = -1;
// Signals
dbus_bool_t loopLatch = true;
DBusMessage* signalMessage = NULL;
// Data frames received
unsigned char buffer[MAX_TAM_INPUT_BUFFER];
int bytes_read = 0;
// Loop latches
bool hdpDataLoopLatch = true;

/*****************************************************************************************************************************
 * Inner functions ***********************************************************************************************************
 ****************************************************************************************************************************/
/* Show error messages */
void showError() {
	printf("dBus Connection error (name): %s\n",dbusErrors.name);
	printf("dBus Connection error (message): %s\n",dbusErrors.message);
	// Frees the memory where the erros has been stored and then reinitializes the error
	dbus_error_free(&dbusErrors);
}

/* Reset and free some global references */
void resetAll() {
	dbus_message_unref(dbusMessageSent);
	dbus_message_unref(dbusMessageReceived);
}

/* Add variant data type to dict parameters */
static void addVariantToDict(DBusMessageIter *iter,int type,void *val) {
  DBusMessageIter value;
  char variantSignature[2] = {type,'\0'};
  // Remember: Variant type is a container. For variants, the "contained_signature" should be the type of the single value
  // inside the variant
  dbus_message_iter_open_container(iter,DBUS_TYPE_VARIANT,variantSignature,&value);
  dbus_message_iter_append_basic(&value,type,val);
  dbus_message_iter_close_container(iter,&value);
}

/* Add each entry (couple of values: key,value) at the corresponding container dict */
static void addEntryToDict(DBusMessageIter *dict,const char *key,int type,void *val) {
  DBusMessageIter entry;
  // Entries with string data type values should not containt NULL values
  if(type == DBUS_TYPE_STRING) {
    const char *str = *((const char **) val);
    if(str == NULL)
      return;
  }
  // According to dBus documentation, for dict entries, "contained_signature" value should be NULL
  dbus_message_iter_open_container(dict,DBUS_TYPE_DICT_ENTRY,NULL,&entry);
  // Appends a basic type to the container (key)
  dbus_message_iter_append_basic(&entry,DBUS_TYPE_STRING,&key);
  // Appends a basic type to the container (value). Take into account that Variant are containers as itself
  addVariantToDict(&entry,type,val);
  dbus_message_iter_close_container(dict,&entry);
}

/* */
static int createDictParameter(DBusMessageIter *iter,dbus_uint16_t dataType,const char *role,const char *description,const char *channelType) {
	// Dbus message subiterator
	DBusMessageIter dict;
	// Dict type is serialized inside dBus systems as arrays of dicts entries. A dict entry is always a struct with two values: {key,value}.
	// Open the container and specify the data type of each tuple. In this case: {key,value} = {String,Variant} = a{sv} = array of "string,variant" tuples
	if(!dbus_message_iter_open_container(iter,DBUS_TYPE_ARRAY,
										 DBUS_DICT_ENTRY_BEGIN_CHAR_AS_STRING
										 DBUS_TYPE_STRING_AS_STRING
										 DBUS_TYPE_VARIANT_AS_STRING
										 DBUS_DICT_ENTRY_END_CHAR_AS_STRING,&dict))
		return false;
	// Add tuples to the container. "DataType", "Role", "Description" and "ChannelType" are the exactly values that must be used by us
	addEntryToDict(&dict,"DataType",DBUS_TYPE_UINT16,&dataType);
	addEntryToDict(&dict,"Role",DBUS_TYPE_STRING,&role);
	addEntryToDict(&dict,"Description",DBUS_TYPE_STRING,&description);
	addEntryToDict(&dict,"ChannelType",DBUS_TYPE_STRING,&channelType);
	// Close the container (tuples appended to it)
	if(!dbus_message_iter_close_container(iter,&dict))
		return false;
	// Dict parameter successfully created
	return true;
}

/* Call remote object methods */
bool callRemoteMethod(char *serviceName,char *adapterPath,char *interfaceName,char *methodName) {
	// Message to be sent or received over dbus connections
	dbusMessageSent = dbus_message_new_method_call(serviceName,adapterPath,interfaceName,methodName);
	if(dbusMessageSent == NULL) {
		printf("(DBUS error\n");
		return false;
	}
	return true;
}

/* Show main list of properties of those object paths passed as arguments (device, adapter, etc) */
void showProperties(char *adapterPath,char *interfaceName) {
	// Attributes
	// Message to be sent or received over dbus connections
	DBusMessage *localDbusMessageSent, *localDbusMessageReceived;
	// Message iterators
	DBusMessageIter messageIterator,dictIterator,keyIterator,valueIterator,subArrayIterator;
	// Message for get the reply {key,value}
	DBusPendingCall* pendingMessage;
	// Dict argument type
	int argType;
	// Dict parameter keys
	char *key;
	// Dict parameter values
	char *strValue;
	dbus_uint16_t uint16Value;
	dbus_uint32_t uint32Value;
	dbus_bool_t booleanValue;
	// Getting all properties from adapter
	localDbusMessageSent = dbus_message_new_method_call(SERVICE_NAME,adapterPath,interfaceName,"GetProperties");
	if(dbus_error_is_set(&dbusErrors))
		showError();
	if(localDbusMessageSent == NULL)
		printf("dBus message sent = NULL. Try again...\n");
	// Initializes a dBusmessageiterator for appending arguments to the output message
	dbus_message_iter_init_append(localDbusMessageSent,&messageIterator);
	// Unmarshalling dict arguments. Send message to dBus system and get a handle for manage replies
	if(!dbus_connection_send_with_reply(dbusConnection,localDbusMessageSent,&pendingMessage,timeOut)) {
		printf("Out of memory. Try again...\n");
		dbus_message_unref(localDbusMessageSent);
	}
	if(pendingMessage == NULL) {
		printf("Pending message (response) NULL. Try again...\n");
		dbus_message_unref(localDbusMessageSent);
	}
	if(dbus_error_is_set(&dbusErrors))
		showError();
	// Block until the outgoing message queue is empty
	dbus_connection_flush(dbusConnection);
	// Free message
	dbus_message_unref(localDbusMessageSent);
	// Block until the pending call is completed
	dbus_pending_call_block(pendingMessage);
	// Get the reply message or null if none has been received yet
	localDbusMessageReceived = dbus_pending_call_steal_reply(pendingMessage);
	if(localDbusMessageReceived == NULL)
		printf("Reply Null\n");
	// Free the pendingMessage message handle
	dbus_pending_call_unref(pendingMessage);
	// Read the input parameters from the message received
	if(!dbus_message_iter_init(localDbusMessageReceived,&messageIterator)) {
		printf("Message received has no arguments\n");
	} else {
		// Array level
		if(dbus_message_iter_get_arg_type(&messageIterator) == DBUS_TYPE_ARRAY) {
			// Type code '101' is reserved to represent the concept of a dict or dict-entry
			if(dbus_message_iter_get_element_type(&messageIterator) == 101) {
				// Dict level
				dbus_message_iter_recurse(&messageIterator,&dictIterator);
				while((argType = dbus_message_iter_get_arg_type(&dictIterator)) != DBUS_TYPE_INVALID) {
					dbus_message_iter_recurse(&dictIterator,&keyIterator);
					// key
					dbus_message_iter_get_basic(&keyIterator,&key);
					dbus_message_iter_next(&keyIterator);
					dbus_message_iter_recurse(&keyIterator,&valueIterator);
					argType = dbus_message_iter_get_arg_type(&valueIterator);
					// Value
					if(argType == DBUS_TYPE_STRING) {
						dbus_message_iter_get_basic(&valueIterator,&strValue);
						printf("%s: %s\n",key,strValue);
					} else if(argType == DBUS_TYPE_OBJECT_PATH) {
						dbus_message_iter_get_basic(&valueIterator,&strValue);
						printf("%s: %s\n",key,strValue);
						returnedObjectPathValueTemp = strValue;
					} else if(argType == DBUS_TYPE_UINT16) {
						dbus_message_iter_get_basic(&valueIterator,&uint16Value);
						printf("%s: %d\n",key,uint16Value);
					} else if(argType == DBUS_TYPE_UINT32) {
						dbus_message_iter_get_basic(&valueIterator,&uint32Value);
						printf("%s: %d\n",key,uint32Value);
					} else if(argType == DBUS_TYPE_BOOLEAN) {
						dbus_message_iter_get_basic(&valueIterator,&booleanValue);
						if(booleanValue == 1)
							printf("%s: %s\n",key,"TRUE");
						else
							printf("%s: %s\n",key,"FALSE");
					} else if(argType == DBUS_TYPE_ARRAY) {
						dbus_message_iter_recurse(&valueIterator,&subArrayIterator);
						// key
						printf("%s: ",key);
						// Iterate to get values
						argType = dbus_message_iter_get_arg_type(&subArrayIterator);
						if(argType == DBUS_TYPE_INVALID)
							printf("\n");
						while((argType = dbus_message_iter_get_arg_type(&subArrayIterator)) != DBUS_TYPE_INVALID) {
							dbus_message_iter_get_basic(&subArrayIterator,&strValue);
							printf("%s\n",strValue);
							dbus_message_iter_next(&subArrayIterator);
						}
					}
					dbus_message_iter_next(&dictIterator);
				}
			}
		}
	}
	// Free resources
	dbus_message_unref(localDbusMessageReceived);
}

/* Call remote object methods with a variable number of arguments */
bool sendMessage(const int args,...) {
	// String case
	char *stringValue;
	// Variant case
	DBusMessageIter variantIterator;
	char variantSignature[2] = {variantDataTypeArg,'\0'};
	void *variantValue;
	if(dbusMessageSent != NULL) {
		if(args > 0) {
			// Manage the variable number of arguments
			va_list argsVar;
			va_start(argsVar,args);
			// Append basic data type arguments
			dbus_message_iter_init_append(dbusMessageSent,&messageIterator);
			for(int i = 0; i < args; i++) {
				switch(dataTypeArgs[i]) {
				case 115:
					// String or objetc path data types
					stringValue = va_arg(argsVar,char*);
					if(!dbus_message_iter_append_basic(&messageIterator,dataTypeArgs[i],&stringValue)) {
						printf("Out of memory...\n");
						return false;
					}
					break;
				case 111:
					// String or objetc path data types
					stringValue = va_arg(argsVar,char*);
					if(!dbus_message_iter_append_basic(&messageIterator,dataTypeArgs[i],&stringValue)) {
						printf("Out of memory...\n");
						return false;
					}
					break;
				case 97:
					// Array data type
					break;
				case 118:
					// Variant data type
					variantValue = va_arg(argsVar,void*);
					dbus_message_iter_open_container(&messageIterator,dataTypeArgs[i],variantSignature,&variantIterator);
					dbus_message_iter_append_basic(&variantIterator,variantDataTypeArg,&variantValue);
					dbus_message_iter_close_container(&messageIterator,&variantIterator);
					break;
				case 101:
				case 123:
				case 125:
					// Dict entry data type
					break;
				case 0:
				default:
					// Invalid data type
					printf("invalid\n");
					break;
				}
			}
			va_end(argsVar);
		}
		// Send message and get a handle for a reply
		if(!dbus_connection_send_with_reply(dbusConnection,dbusMessageSent,&pendingMessage,timeOut)) {
			printf("Out of memory...\n");
			dbusMessageReceived = NULL;
			return false;
		}
		if(pendingMessage == NULL) {
			printf("Pending message NULL...\n");
			dbusMessageReceived = NULL;
			return false;
		}
		dbus_connection_flush(dbusConnection);
		// Block until we recieve a reply
		dbus_pending_call_block(pendingMessage);
		// Get the reply message
		dbusMessageReceived = dbus_pending_call_steal_reply(pendingMessage);
	}
	return true;
}

/* Read input parameters and get the reply message received from dbus */
bool readInputParameters() {
	// Free the pending message handle
	dbus_pending_call_unref(pendingMessage);
	// Read the parameters
	if(!dbus_message_iter_init(dbusMessageReceived,&messageIterator)) {
		printf("Received message has no input arguments\n");
		return false;
	} else if(dbus_message_iter_get_arg_type(&messageIterator) == DBUS_TYPE_STRING) {
		// Expected value: string
		dbus_message_iter_get_basic(&messageIterator,&returnedStringValueTemp);
		//printf("%s\n",returnedStringValueTemp);
		return true;
	} else if(dbus_message_iter_get_arg_type(&messageIterator) == DBUS_TYPE_OBJECT_PATH) {
		// Expected value: object
		dbus_message_iter_get_basic(&messageIterator,&returnedStringValueTemp);
		//printf("%s\n",returnedStringValueTemp);
		return true;
	} else if(dbus_message_iter_get_arg_type(&messageIterator) == DBUS_TYPE_INVALID) {
		// Expected value: invalid
		printf("Invalid data type received\n");
		return false;
	} else {
		// Expected value: undeterminated
		printf("Warning, unexpected data type received\n");
		printf("%d\n",dbus_message_iter_get_arg_type(&messageIterator));
		return false;
	}
}

/* Set data type array of input arguments */
void setDataTypeArgsArray(const int args,...) {
	// Manage the variable number of arguments
	if(args <= MAX_DATA_TYPE_ARGS) {
		va_list argsVar;
		va_start(argsVar,args);
		for(int i = 0; i < args; i ++)
			dataTypeArgs[i] = va_arg(argsVar,int);
		va_end(argsVar);
	} else
		printf("Error adding data types to array. Number of input arguments higher than expected\n");
}

/* Configuration of global attributes and DBUS system */
bool init() {
	// Initializes dbusError structure (name, message, others...)
	dbus_error_init(&dbusErrors);
	// Open a connection to the system bus
	dbusConnection = dbus_bus_get(DBUS_BUS_SYSTEM,&dbusErrors);
	// Checks if any error ocurred (returns TRUE, if so)
	if(dbus_error_is_set(&dbusErrors)) showError();
	if(dbusConnection == NULL) {
		printf("DBUS error");
		return false;
	}
	// Output (everything ok)
	return true;
}

/* Check the local bluetooth adapter availability */
bool setDefaultBluetoothAdapter() {
	// New message to invoke a method on a remote object
	callRemoteMethod(SERVICE_NAME,MANAGER_OBJECT_PATH,MANAGER_INTERFACE_NAME,"DefaultAdapter");
	// Checks if any error ocurred (returns TRUE, if so)
	if(dbus_error_is_set(&dbusErrors)) showError();
	if(dbusMessageSent == NULL) {
		return false;
	} else {
		// Sends the message and blocks a certain time while waiting for a reply
		sendMessage(0);
		// Gets arguments from a message given a variable argument list
		dbus_message_get_args(dbusMessageReceived,&dbusErrors,DBUS_TYPE_OBJECT_PATH,&defaultAdapterPath,DBUS_TYPE_INVALID);
	}
	return true;
}

/* Get the right data type to each Continua Health Device passed as input argument */
dbus_uint16_t getContinuaDeviceDataType(char *str) {
	if(strcmp("PulseOximeter",str) == 0)
		return 0x1004;
	else if(strcmp("Electrocardiograph",str) == 0)
		return 0x1006;
	else if(strcmp("BloodPressureMonitor",str) == 0)
		return 0x1007;
	else if(strcmp("Thermometer",str) == 0)
		return 0x1008;
	else if(strcmp("WeightingScale",str) == 0)
		return 0x100F;
	else if(strcmp("GlucoseMeter",str) == 0)
		return 0x1011;
	else if(strcmp("INRMonitor",str) == 0)
		return 0x1012;
	else if(strcmp("InsulinePump",str) == 0)
		return 0x1013;
	else if(strcmp("BodyCompositionAnalyzer",str) == 0)
		return 0x1014;
	else if(strcmp("PeakFlowMonitor",str) == 0)
		return 0x1015;
	else if(strcmp("CardiovascularFitness",str) == 0)
		return 0x1029;
	else if(strcmp("StrengthFitnessEquipment",str) == 0)
		return 0x102A;
	else if(strcmp("PhysicalActivityMonitor",str) == 0)
		return 0x102B;
	else if(strcmp("IndependentLivingActivityHub",str) == 0)
		return 0x1047;
	else if(strcmp("MedicationMonitor",str) == 0)
		return 0x1048;
	else
		return -1;
}

/*****************************************************************************************************************************
 * Native functions **********************************************************************************************************
 ****************************************************************************************************************************/
/* Start DBUS platform (init function) */
JNIEXPORT jboolean JNICALL Java_org_universAAL_continua_weighingscale_publisher_hdpManager_startDbusPlatform(JNIEnv *env,jobject obj) {
	if(init())
		return JNI_TRUE;
	else
		return JNI_FALSE;
}

/* Return local bluetooth adapter path if exists (and it is ready), null otherwise */
JNIEXPORT jstring JNICALL Java_org_universAAL_continua_weighingscale_publisher_hdpManager_getLocalBluetoothAdapterPath(JNIEnv *env,jobject obj) {
	defaultAdapterPath = NULL;
	// New message to invoke a method on a remote object
	callRemoteMethod(SERVICE_NAME,MANAGER_OBJECT_PATH,MANAGER_INTERFACE_NAME,"DefaultAdapter");
	// Checks if any error ocurred (returns TRUE, if so)
	if(dbus_error_is_set(&dbusErrors)) showError();
	if(dbusMessageSent == NULL) {
		printf("DBUS error\n");
	} else {
		// Sends the message and blocks a certain time while waiting for a reply
		sendMessage(0);
		// Gets arguments from a message given a variable argument list
		dbus_message_get_args(dbusMessageReceived,&dbusErrors,DBUS_TYPE_OBJECT_PATH,&defaultAdapterPath,DBUS_TYPE_INVALID);
	}
	return ((*env) -> NewStringUTF(env,defaultAdapterPath));
}

/* List all local bluetooth adapters available at PC */
JNIEXPORT void JNICALL Java_org_universAAL_continua_weighingscale_publisher_hdpManager_showLocalBluetoothAdaptersPath(JNIEnv *env,jobject obj) {
	showProperties("/",MANAGER_INTERFACE_NAME);
}

/* List all default local bluetooth adapter properties */
JNIEXPORT void JNICALL Java_org_universAAL_continua_weighingscale_publisher_hdpManager_showDefaultLocalBluetoothAdaptersProperties(JNIEnv *env,jobject obj) {
	if(defaultAdapterPath == NULL) {
		if(setDefaultBluetoothAdapter()) {
			showProperties(defaultAdapterPath,ADAPTER_INTERFACE_NAME);
		}
	} else {
		showProperties(defaultAdapterPath,ADAPTER_INTERFACE_NAME);
	}
}

/* Get the object path of a remote device given a MAC address. The object of the remote device must be first created */
JNIEXPORT jstring JNICALL Java_org_universAAL_continua_weighingscale_publisher_hdpManager_getRemoteBluetoothAdapterPath(JNIEnv *env,jobject obj,jstring macAddressRemoteDevice) {
	returnedStringValueTemp = NULL;
	remoteDevicePath = NULL;
	const jbyte *inputString;
	inputString = (*env) -> GetStringUTFChars(env,macAddressRemoteDevice,NULL);
	if(inputString == NULL) {
		printf("Out memory error\n");
		return NULL;
	}
	// Create a new object path for the selected remote device
	if(defaultAdapterPath == NULL)
		if(!setDefaultBluetoothAdapter()) {
			return NULL;
		}
	callRemoteMethod(SERVICE_NAME,defaultAdapterPath,ADAPTER_INTERFACE_NAME,"CreateDevice");
	if(dbusMessageSent == NULL) {
		printf("DBUS error\n");
		return NULL;
	}
	setDataTypeArgsArray(1,DBUS_TYPE_STRING);
	sendMessage(1,inputString);
	if(dbusMessageReceived == NULL) {
		printf("DBUS error\n");
		return NULL;
	}
	callRemoteMethod(SERVICE_NAME,defaultAdapterPath,ADAPTER_INTERFACE_NAME,"FindDevice");
	if(dbusMessageSent == NULL) {
		printf("DBUS error\n");
		return NULL;
	}
	setDataTypeArgsArray(1,DBUS_TYPE_STRING);
	sendMessage(1,inputString);
	if(dbusMessageReceived == NULL) {
		printf("DBUS error\n");
		return NULL;
	}
	// Release native string resources
	if(readInputParameters()) {
		(*env) -> ReleaseStringUTFChars(env,macAddressRemoteDevice,inputString);
		remoteDevicePath = returnedStringValueTemp;
	}
	// Return value
	return (*env) -> NewStringUTF(env,returnedStringValueTemp);
}

/* Show all properties of a remote device (the remote device should be first created) */
JNIEXPORT void JNICALL Java_org_universAAL_continua_weighingscale_publisher_hdpManager_showRemoteBluetoothAdapterProperties(JNIEnv *env,jobject obj,jstring inputRemoteDevicePath) {
	char *strTemp = NULL;
	const jbyte *inputString;
	inputString = (*env) -> GetStringUTFChars(env,inputRemoteDevicePath,NULL);
	if(inputString == NULL)
		printf("Out memory error\n");
	else {
		strTemp = inputString;
		showProperties(strTemp,DEVICE_INTERFACE_NAME);
		(*env) -> ReleaseStringUTFChars(env,inputRemoteDevicePath,inputString);
	}
}

/* Change the value of the specific property: Trusted, Blocked, Alias */
JNIEXPORT void JNICALL Java_org_universAAL_continua_weighingscale_publisher_hdpManager_setPropertyRemoteDevice(JNIEnv *env, jobject obj,jstring inputRemoteDevicePath,jstring propertyKey,jboolean propertyValue) {
	// Get values from input parameters
	const jbyte *inputStringPath;
	const jbyte *inputStringProperty;
	jboolean inputBoolean;
	inputStringPath = (*env) -> GetStringUTFChars(env,inputRemoteDevicePath,NULL);
	inputStringProperty = (*env) -> GetStringUTFChars(env,propertyKey,NULL);
	if((inputStringPath == NULL)||(inputStringProperty == NULL)) {
		printf("Out memory error\n");
	} else {
		if(propertyValue == true)
			inputBoolean = JNI_TRUE;
		else
			inputBoolean = JNI_FALSE;
		// Go
		callRemoteMethod(SERVICE_NAME,inputStringPath,DEVICE_INTERFACE_NAME,"SetProperty");
		if(dbusMessageSent == NULL)
			printf("DBUS error\n");
		else {
			// Set the expected data type
			variantDataTypeArg = DBUS_TYPE_BOOLEAN;
			setDataTypeArgsArray(2,DBUS_TYPE_STRING,DBUS_TYPE_VARIANT);
			// Sends the message and blocks a certain time while waiting for a reply
			sendMessage(2,inputStringProperty,inputBoolean);
			if(dbusMessageReceived == NULL)
				printf("DBUS error\n");
			else
				printf("Property %s successfully changed\n",inputStringProperty);
			// Release resources
			(*env) -> ReleaseStringUTFChars(env,inputRemoteDevicePath,inputStringPath);
			(*env) -> ReleaseStringUTFChars(env,inputRemoteDevicePath,inputStringProperty);
		}
	}
}

/* Create HDP application and gets the object path of the new created application */
JNIEXPORT jstring JNICALL Java_org_universAAL_continua_weighingscale_publisher_hdpManager_createHDPApplication(JNIEnv *env, jobject obj,jstring dataTypeValue,jstring roleValue,jstring shortDescriptionValue,jstring channelTypeValue) {
	hdpApplicationPath = NULL;
	dbus_uint16_t dataTypeTemp;
	// Input arguments
	const jbyte *inputStringData;
	const jbyte *inputStringRole;
	const jbyte *inputStringDescription;
	const jbyte *inputStringChannel;
	inputStringData = (*env) -> GetStringUTFChars(env,dataTypeValue,NULL);
	inputStringRole = (*env) -> GetStringUTFChars(env,roleValue,NULL);
	inputStringDescription = (*env) -> GetStringUTFChars(env,shortDescriptionValue,NULL);
	inputStringChannel = (*env) -> GetStringUTFChars(env,channelTypeValue,NULL);
	if((inputStringData == NULL)||(inputStringRole == NULL)||(inputStringDescription == NULL)||(inputStringChannel == NULL)) {
		printf("Out memory error\n");
		return NULL;
	}
	// Create HDP application
	callRemoteMethod(SERVICE_NAME,HEALTH_MANAGER_OBJECT_PATH,HEALTH_MANAGER_INTERFACE_NAME,"CreateApplication");
	if(dbusMessageSent == NULL) {
		printf("DBUS error\n");
		return NULL;
	}
	dataTypeTemp = getContinuaDeviceDataType(inputStringData);
	if(dataTypeTemp == -1)
		return NULL;
	// Initializes a dBusmessageiterator for appending arguments to the output message
	dbus_message_iter_init_append(dbusMessageSent,&messageIterator);
	// Marshalling dict arguments (read health-api.txt doc) with appropriate data type
	if(!createDictParameter(&messageIterator,dataTypeTemp,inputStringRole,inputStringDescription,inputStringChannel)) {
		printf("DBUS error\n");
		dbus_message_unref(dbusMessageSent);
		return NULL;
	}
	// Send message to dBus system and get a handle for manage replies
	sendMessage(0);
	if(dbusMessageReceived == NULL) {
		printf("DBUS error\n");
		return NULL;
	}
	// Release native string resources
	if(readInputParameters()) {
		(*env) -> ReleaseStringUTFChars(env,dataTypeValue,inputStringData);
		(*env) -> ReleaseStringUTFChars(env,roleValue,inputStringRole);
		(*env) -> ReleaseStringUTFChars(env,shortDescriptionValue,inputStringDescription);
		(*env) -> ReleaseStringUTFChars(env,channelTypeValue,inputStringChannel);
	}
	// Return value
	hdpApplicationPath = returnedStringValueTemp;
	return (*env) -> NewStringUTF(env,hdpApplicationPath);
}

/* Closes the HDP application identified by the object path passed as argument */
JNIEXPORT void JNICALL Java_org_universAAL_continua_weighingscale_publisher_hdpManager_destroyHDPApplication(JNIEnv *env, jobject obj,jstring hdpInputApplicationPath) {
	// Get values from input parameters
	const jbyte *inputString;
	inputString = (*env) -> GetStringUTFChars(env,hdpInputApplicationPath,NULL);
	if(inputString == NULL)
		printf("Out memory error\n");
	else {
		// Go
		callRemoteMethod(SERVICE_NAME,HEALTH_MANAGER_OBJECT_PATH,HEALTH_MANAGER_INTERFACE_NAME,"DestroyApplication");
		if(dbusMessageSent == NULL)
			printf("DBUS error\n");
		else {
			// Set the expected data type
			setDataTypeArgsArray(1,DBUS_TYPE_OBJECT_PATH);
			// Sends the message and blocks a certain time while waiting for a reply
			sendMessage(1,inputString);
			if(dbusMessageReceived == NULL)
				printf("DBUS error\n");
			else
				printf("HDP application (%s) successfully closed\n",inputString);
			// Release native string resources
			(*env) -> ReleaseStringUTFChars(env,hdpInputApplicationPath,inputString);
			hdpApplicationPath = NULL;
		}
	}
}

/* Get the HDP channel path ready to start the communication */
JNIEXPORT jstring JNICALL Java_org_universAAL_continua_weighingscale_publisher_hdpManager_getHDPDataChannelPath(JNIEnv *env,jobject obj,jstring inputRemoteDevicePath) {
	hdpChannelPath = NULL;
	returnedObjectPathValueTemp = NULL;
	// Get values from input parameters
	const jbyte *inputString;
	inputString = (*env) -> GetStringUTFChars(env,inputRemoteDevicePath,NULL);
	if(inputString == NULL) {
		printf("Out memory error\n");
		return NULL;
	} else {
		showProperties(inputString,HEALTH_DEVICE_INTERFACE_NAME);
		(*env) -> ReleaseStringUTFChars(env,inputRemoteDevicePath,inputString);
		hdpChannelPath = returnedObjectPathValueTemp;
	}
	return (*env) -> NewStringUTF(env,returnedObjectPathValueTemp);
}

/* Check the availability of the DBUS system */
JNIEXPORT jboolean JNICALL Java_org_universAAL_continua_weighingscale_publisher_hdpManager_getDbusSystemAvailability(JNIEnv *env,jobject obj) {
	if(!dbus_connection_get_is_connected(dbusConnection))
		return JNI_FALSE;
	else
		return JNI_TRUE;
}

/* Add a specific rule to follow HDP messages inside DBUS */
JNIEXPORT jboolean JNICALL Java_org_universAAL_continua_weighingscale_publisher_hdpManager_enableHDPListener(JNIEnv *env,jobject obj) {
	dbus_bus_add_match(dbusConnection,"type='signal',interface='org.bluez.HealthDevice'",&dbusErrors);
	dbus_connection_flush(dbusConnection);
	if(dbus_error_is_set(&dbusErrors)) {
		showError();
		return JNI_FALSE;
	} else
		return JNI_TRUE;
}

/* */
JNIEXPORT void JNICALL Java_org_universAAL_continua_weighingscale_publisher_hdpManager_waitHDPConnections(JNIEnv *env,jobject obj) {
	jclass javaClass;
	jmethodID javaMethodIdentifier;
	while(loopLatch) {
		printf("Waiting for source connections...\n");
		// This function will block until it can read or write (messages may remain in the incoming queue)
		dbus_connection_read_write(dbusConnection,timeOut);
		// Get the first received message from the incoming message queue removing it from the queue
		signalMessage = dbus_connection_pop_message(dbusConnection);
		if(signalMessage == NULL)
			continue;
		// Check if the message is a signal from the correct interface and with the correct function name (read health-api.txt file)
		if(dbus_message_is_signal(signalMessage,"org.bluez.HealthDevice","ChannelConnected")) {
			printf("Channel connected\n");
			hdpDataLoopLatch = true;
			javaClass = (*env) -> GetObjectClass(env,obj);
			javaMethodIdentifier =	(*env) -> GetMethodID(env,javaClass,"onChannelConnected","()V");
			if(javaMethodIdentifier == NULL) {
				// Method not found at Java object
				return;
			}
			(*env) -> CallVoidMethod(env,obj,javaMethodIdentifier);
		} else if(dbus_message_is_signal(signalMessage,"org.bluez.HealthDevice","ChannelDeleted")) {
			printf("Channel disconnected\n");
			hdpDataLoopLatch = false;
			loopLatch = true;
			javaClass = (*env) -> GetObjectClass(env,obj);
			javaMethodIdentifier =	(*env) -> GetMethodID(env,javaClass,"onChannelDisconnected","()V");
			if(javaMethodIdentifier == NULL) {
				// Method not found at Java object
				return;
			}
			(*env) -> CallVoidMethod(env,obj,javaMethodIdentifier);
continue;
		}
		// Free resources
//		dbus_message_unref(signalMessage);
signalMessage = NULL;
	}
}

/* Show the HDP channel available properties: Type (quality of service of the data channel), Device (remote device that is connected with) and
 * Application (application which this channel is related to) */
JNIEXPORT void JNICALL Java_org_universAAL_continua_weighingscale_publisher_hdpManager_showHDPDataChannelProperties(JNIEnv *env, jobject obj,jstring inputHDPChannelPath) {
	char *strTemp = NULL;
	const jbyte *inputString;
	inputString = (*env) -> GetStringUTFChars(env,inputHDPChannelPath,NULL);
	if(inputString == NULL)
		printf("Out memory error\n");
	else {
		strTemp = inputString;
		showProperties(strTemp,HEALTH_CHANNEL_INTERFACE_NAME);
		(*env) -> ReleaseStringUTFChars(env,inputHDPChannelPath,inputString);
	}
}

/* Return the file descriptor of this HDP data channel */
JNIEXPORT jint JNICALL Java_org_universAAL_continua_weighingscale_publisher_hdpManager_getHDPDataChannelFileDescriptor(JNIEnv *env, jobject obj,jstring inputHDPChannelPath) {
	const jbyte *inputString;
	inputString = (*env) -> GetStringUTFChars(env,inputHDPChannelPath,NULL);
	if(inputString == NULL) {
		printf("Out memory error\n");
		return -1;
	} else {
		callRemoteMethod(SERVICE_NAME,inputString,HEALTH_CHANNEL_INTERFACE_NAME,"Acquire");
		// Sends the message and blocks a certain time while waiting for a reply
		sendMessage(0);
		// Gets arguments from a message given a variable argument list
		dbus_message_get_args(dbusMessageReceived,&dbusErrors,DBUS_TYPE_UNIX_FD,&fileDescriptor,DBUS_TYPE_INVALID);
		// Release resources
		(*env) -> ReleaseStringUTFChars(env,inputHDPChannelPath,inputString);
		// Return data
		if(fileDescriptor > 0)
			return fileDescriptor;
		else
			return -1;
	}
}

/* Release the file descriptor (HDP application will be closed too) */
JNIEXPORT void JNICALL Java_org_universAAL_continua_weighingscale_publisher_hdpManager_releaseHDPDataChannelFileDescriptor(JNIEnv *env, jobject obj,jstring inputHDPChannelPath) {
	const jbyte *inputString;
	inputString = (*env) -> GetStringUTFChars(env,inputHDPChannelPath,NULL);
	if(inputString == NULL) {
		printf("Out memory error\n");
		return;
	} else {
		callRemoteMethod(SERVICE_NAME,inputString,HEALTH_CHANNEL_INTERFACE_NAME,"Release");
		// Sends the message and blocks a certain time while waiting for a reply
		sendMessage(0);
		if(dbusMessageReceived == NULL)
			printf("DBUS error\n");
		else {
			printf("File descriptor successfully released\n");
			fileDescriptor = -1;
			hdpChannelPath = NULL;
		}
		// Release resources
		(*env) -> ReleaseStringUTFChars(env,inputHDPChannelPath,inputString);
	}
}

/* */
JNIEXPORT void JNICALL Java_org_universAAL_continua_weighingscale_publisher_hdpManager_waitHDPDataFrames(JNIEnv *env,jobject obj,jint inputFileDescriptor) {
	// FD settings
	fd_set readfs;
	int retval;
	struct timeval tv;
	jclass javaClass;
	jmethodID javaMethodIdentifier;
	while(hdpDataLoopLatch) {
		// Waiting input frames timeout (60 seconds)
		tv.tv_sec = 60;
		tv.tv_usec = 0;
		// Setting FD flags
		FD_ZERO(&readfs);
		FD_SET(inputFileDescriptor,&readfs);
		fcntl(inputFileDescriptor,F_SETFL,O_ASYNC|O_NONBLOCK);
		printf("Waiting for input data frames\n");
		// Next line will block the application until data is available at the right FD or timeout ends
		retval = select(inputFileDescriptor+1,&readfs,NULL,NULL,&tv);
		if(retval == -1) {
			hdpDataLoopLatch = false;
			continue;
		} else if(retval) {
			for(int i=0; i<MAX_TAM_INPUT_BUFFER; i++) buffer[i] = '\0';
			bytes_read = recv(inputFileDescriptor,buffer,MAX_TAM_INPUT_BUFFER,0);
			buffer[bytes_read] = '\0';
			// Association response not received ('abort' frame received from agent)
			if (bytes_read == 0){
				hdpDataLoopLatch = false;
				continue;
			} else {
				// Callback
				printf("Data received\n");
				javaClass = (*env) -> GetObjectClass(env,obj);
				javaMethodIdentifier =	(*env) -> GetMethodID(env,javaClass,"onDataReceived","()V");
				if(javaMethodIdentifier == NULL) {
					// Method not found at Java object
					return;
				}
				(*env) -> CallVoidMethod(env,obj,javaMethodIdentifier);
				if(buffer[0] == 230) {
					hdpDataLoopLatch = false;
					continue;
				}
			}
		} else {
			printf("Timeout\n");
			hdpDataLoopLatch = false;
			continue;
		}
	}
}

/* */
JNIEXPORT jbyteArray JNICALL Java_org_universAAL_continua_weighingscale_publisher_hdpManager_getHDPDataFrame(JNIEnv *env,jobject obj,jint inputFileDescriptor) {
	jbyteArray outputBuffer = NULL;
	if((fileDescriptor == inputFileDescriptor)||(bytes_read != 0)) {
		outputBuffer = (*env) -> NewByteArray(env,bytes_read);
		if(outputBuffer != NULL)
		// Important ensure and know the size to be copied in order to avoid an index overflow exception
			(*env) -> SetByteArrayRegion(env,outputBuffer,0,bytes_read,buffer);
		return outputBuffer;
	} else
		return NULL;
}

/* Number of bytes received in an HDP data frame */
JNIEXPORT jint JNICALL Java_org_universAAL_continua_weighingscale_publisher_hdpManager_getSizeHDPDataFrame(JNIEnv *env,jobject obj,jint inputFileDescriptor) {
	if(fileDescriptor == inputFileDescriptor)
		return bytes_read;
	else
		return -1;
}

/* Show HDP received data frame (bytes in hex mode) */
JNIEXPORT void JNICALL Java_org_universAAL_continua_weighingscale_publisher_hdpManager_showHDPDataFrame(JNIEnv *env,jobject obj,jint inputFileDescriptor) {
	if((fileDescriptor == inputFileDescriptor)||(bytes_read != 0)) {
		for(int i=0;i<bytes_read;i++) {
			printf("0x");
			// Show unsigned hexadecimal integer in capital letters
			if(i % 2 == 0)
				printf("%02X ",buffer[i]);
			else
				printf("%02X\n",buffer[i]);
		}
	} else
		printf("Wrong file descriptor number or data not available\n");
}

/* */
JNIEXPORT void JNICALL Java_org_universAAL_continua_weighingscale_publisher_hdpManager_sendHDPDataToDevice(JNIEnv *env,jobject obj,jint inputFileDescriptor,jbyteArray inputDataFrame) {
	if((fileDescriptor == inputFileDescriptor)&&(inputDataFrame != 0)) {
		int sizeFrame = (*env) -> GetArrayLength(env,inputDataFrame);
		jbyte dataFrame[sizeFrame];
		(*env) -> GetByteArrayRegion(env,inputDataFrame,0,sizeFrame,dataFrame);
		send(inputFileDescriptor,dataFrame,sizeof(dataFrame),0);
		// Manager rejects association request from agent
		if(dataFrame[0] == 0xffffffe3) {
			if (dataFrame[5] == 0x00){
				return;
			}else if (dataFrame[5]==0x03) {
				return;
			} else {
				hdpDataLoopLatch = false;
			}
		}
	} else
		printf("Wrong file descriptor number or input data frame null\n");
}

/* End application and release resources */

JNIEXPORT void JNICALL Java_org_universAAL_continua_weighingscale_publisher_hdpManager_closeHDPManager(JNIEnv *env,jobject obj) {
	loopLatch = false;
	defaultAdapterPath = NULL;
	remoteDevicePath = NULL;
	hdpApplicationPath = NULL;
	hdpChannelPath = NULL;
	returnedStringValueTemp = NULL;
	returnedObjectPathValueTemp = NULL;
	fileDescriptor = -1;
	hdpDataLoopLatch = false;
	dbus_message_unref(dbusMessageSent);
	dbus_message_unref(dbusMessageReceived);	
	dbus_connection_unref(dbusConnection);
}


/*****************************************************************************************************************************
 * EOF ***********************************************************************************************************************
 ****************************************************************************************************************************/
