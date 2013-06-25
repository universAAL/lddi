// El siguiente bloque ifdef muestra la forma est�ndar de crear macros que facilitan 
// la exportaci�n de archivos DLL. Todos los archivos de este archivo DLL se compilan con el s�mbolo HDPNATIVEDLL_EXPORTS
// definido en la l�nea de comandos. Este s�mbolo no debe definirse en ning�n proyecto
// que use este archivo DLL. De este modo, otros proyectos cuyos archivos de c�digo fuente incluyan el archivo 
// interpretan que las funciones HDPNATIVEDLL_API se importan de un archivo DLL, mientras que este archivo DLL interpreta los s�mbolos
// definidos en esta macro como si fueran exportados.
#ifdef HDPNATIVEDLL_EXPORTS
#define HDPNATIVEDLL_API __declspec(dllexport)
#else
#define HDPNATIVEDLL_API __declspec(dllimport)
#endif

// Clase exportada de HDPNativeDll.dll
class HDPNATIVEDLL_API CHDPNativeDll {
public:
	CHDPNativeDll(void);
	// TODO: agregar m�todos aqu�.	
};

extern HDPNATIVEDLL_API int nHDPNativeDll;

HDPNATIVEDLL_API int fnHDPNativeDll(void);
