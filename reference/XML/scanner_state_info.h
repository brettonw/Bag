#ifndef		_SCANNER_STATE_INFO_H_
#define		_SCANNER_STATE_INFO_H_

//------------------------------------------------------------------------------
// class definitions
//------------------------------------------------------------------------------
class ScannerStateInfo
{
public:
		/* void */      ScannerStateInfo (void);
		/* void */      ScannerStateInfo (uInt2 nextState, uInt2 storage, uInt2 action);
		void			operator () (uInt2 nextState, uInt2 storage, uInt2 action);

public:
		const enum { INVALID_STATE = 0xffff };
public:
		uInt2			m_nextState;
		uInt2			m_storage:2;
		uInt2			m_action:14;
};

//------------------------------------------------------------------------------
// inlines
//------------------------------------------------------------------------------
#ifndef		_SCANNER_STATE_INFO_INL_
#include    "scanner_state_info.inl"
#endif	//	_SCANNER_STATE_INFO_INL_

//------------------------------------------------------------------------------

#endif	//	_SCANNER_STATE_INFO_H_
