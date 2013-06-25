// El siguiente bloque ifdef muestra la forma estándar de crear macros que facilitan 
// la exportación de archivos DLL. Todos los archivos de este archivo DLL se compilan con el símbolo HDPNATIVEDLL_EXPORTS
// definido en la línea de comandos. Este símbolo no debe definirse en ningún proyecto
// que use este archivo DLL. De este modo, otros proyectos cuyos archivos de código fuente incluyan el archivo 
// interpretan que las funciones HDPNATIVEDLL_API se importan de un archivo DLL, mientras que este archivo DLL interpreta los símbolos
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
	// TODO: agregar métodos aquí.	
};

extern HDPNATIVEDLL_API int nHDPNativeDll;

HDPNATIVEDLL_API int fnHDPNativeDll(void);
