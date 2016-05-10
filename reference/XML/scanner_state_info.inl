#ifndef		_SCANNER_STATE_INFO_INL_
#define		_SCANNER_STATE_INFO_INL_

//------------------------------------------------------------------------------
//	includes
//------------------------------------------------------------------------------
#ifndef		_SCANNER_STATE_INFO_H_
#include    "scanner_state_info.h"
#endif	//	_SCANNER_STATE_INFO_H_

//------------------------------------------------------------------------------
//	class methods
//------------------------------------------------------------------------------
inline
/* void */
ScannerStateInfo::ScannerStateInfo (void)
{
	m_nextState = INVALID_STATE;
}

//------------------------------------------------------------------------------
inline
/* void */
ScannerStateInfo::ScannerStateInfo (uInt2 nextState, uInt2 storage, uInt2 action) :
	m_nextState (nextState),
	m_storage (storage),
	m_action (action)
{
}

//------------------------------------------------------------------------------
inline
void
ScannerStateInfo::operator () (uInt2 nextState, uInt2 storage, uInt2 action)
{
	m_nextState = nextState;
	m_storage = storage;
	m_action = action;
}

//------------------------------------------------------------------------------

#endif	//	_SCANNER_STATE_INFO_INL_
