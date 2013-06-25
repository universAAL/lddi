// HDPNativeDll.cpp: define las funciones exportadas de la aplicaci�n DLL.
//

#include "stdafx.h"
#include "HDPNativeDll.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// �nico objeto de aplicaci�n

CWinApp theApp;

using namespace std;

int _tmain(int argc, TCHAR* argv[], TCHAR* envp[])
{
	int nRetCode = 0;
		
	HMODULE hModule = ::GetModuleHandle(NULL);

	if (hModule != NULL)
	{
		// Inicializar MFC e imprimir un mensaje en caso de error
		if (!AfxWinInit(hModule, NULL, ::GetCommandLine(), 0))
		{
			// TODO: cambie el c�digo de error en funci�n de sus necesidades.
			_tprintf(_T("Error irrecuperable: error al inicializar MFC\n"));
			nRetCode = 1;
		}
		else
		{
			// TODO: explicar aqu� el c�digo de comportamiento de la aplicaci�n.
			
		}
	}
	else
	{
		// TODO: cambiar el c�digo de error en funci�n de sus necesidades
		_tprintf(_T("Error irrecuperable de GetModuleHandle.\n"));
		nRetCode = 1;
	}

	return nRetCode;
}
