#ifndef		_SCANNER_STATE_INL_
#define		_SCANNER_STATE_INL_

//--------------------------------------------------------------------------
//	includes
//--------------------------------------------------------------------------
#ifndef		_SCANNER_STATE_H_
#include    "scanner_state.h"
#endif	//	_SCANNER_STATE_H_

//--------------------------------------------------------------------------
//	class methods
//--------------------------------------------------------------------------
inline
/* void */
ScannerState::ScannerState (void)
{
}

//--------------------------------------------------------------------------
inline
/* void */
ScannerState::ScannerState (const ScannerState& scannerState)
{
	memcpy (m_stateInfo, scannerState.m_stateInfo, STATE_COUNT * sizeof(ScannerStateInfo));
}

//--------------------------------------------------------------------------
inline
ScannerState&
ScannerState::operator = (const ScannerState& scannerState)
{
	memcpy (m_stateInfo, scannerState.m_stateInfo, STATE_COUNT * sizeof(ScannerStateInfo));
	return *this;
}

//--------------------------------------------------------------------------
inline
const ScannerStateInfo&
ScannerState::operator [] (uInt index) const
{
	return m_stateInfo[index];
}

//--------------------------------------------------------------------------

#endif	//	_SCANNER_STATE_INL_
