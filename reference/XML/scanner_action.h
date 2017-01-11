#ifndef		_SCANNER_ACTION_H_
#define		_SCANNER_ACTION_H_

//------------------------------------------------------------------------------
//	class definitions
//------------------------------------------------------------------------------
class ScannerAction
{
public:
virtual	/* void */      ~ScannerAction (void);
virtual	void            Action (cString buffer, uInt length, uInt2 action, cString nextBuffer, uInt2 nextState) = 0;
};

//------------------------------------------------------------------------------

#endif	//	_SCANNER_ACTION_H_
