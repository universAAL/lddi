This folder contains an Interface to publish in the Context Bus the information of the Zwave devices and an example to consume the information. 

The lddi.zwave.exporter is the service that publishes the information. In order to use the service you need: 

1. Vera Lite gateway from MicasaVerde http://www.micasaverde.com/controllers/veralite/ 
2. Motion Sensors. We have use the one from Everspring  http://www.everspring.com/SP814.aspx
3. Closure Contact Sensor. We have used the one from Everspring http://www.everspring.com/HSM02.aspx
4. Power Sockets. We have use the one from Everspting http://www.everspring.com/Products/HomeAutomation/AN158.aspx

Once you have the devices, you'll need to configure you're Zwave network. 
You'll be able to use the power consumption reading of the lddi.zwave.exporter right away. 
It publishes the current consumption of each Socket every minute. 

In order to take profit of the Motion and Contact sensors, you'll have to do a few configurations in your Vera.
1. Enter in your Vera web application (at the IP address you have assigned to the device)
2. Go to Automation -> New Scene
3. Inside the New Scene, go to Triggers and add a new one
4. Select the device you're creating the scene for and add the trigger for when is tripped
5. Add the following code as Luup Event

	local socket = require("socket")
	local tcp = socket.tcp()
	local Msg = os.date("Contact XXX %c")
	tcp:settimeout(3)
	tcp:connect("192.168.238.41", 53007)
	tcp:send(Msg)
	tcp:close(socket)

6. Replace XXX for a nave you can use to recognize the device in the universAAL environment
7. Save the Scene. 

Repeat these steps for each Motion Sensor and Contact Sensor.

Now you can use the lddi.zwave.exporter in  it's full power. 

