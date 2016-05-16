#ifndef		_SCANNER_STATE_H_
#define		_SCANNER_STATE_H_

//--------------------------------------------------------------------------
// includes
//--------------------------------------------------------------------------
#ifndef		_SCANNER_STATE_INFO_H_
#include	"scanner_state_info.h"
#endif	//	_SCANNER_STATE_INFO_H_

//--------------------------------------------------------------------------
// class definitions
//--------------------------------------------------------------------------
class ScannerState
{
public:
		/* void */                  ScannerState (void);
		/* void */                  ScannerState (const ScannerState& scannerState);
		ScannerState&		        operator = (const ScannerState& scannerState);
        const ScannerStateInfo&     operator [] (uInt index) const;
		bool				        Set (uInt2 input, uInt2 nextState, uInt2 storage, uInt2 action);
		void				        SetAll (uInt2 nextState, uInt2 storage, uInt2 action);
public:
		const enum
		{
			DEFAULT_STATE = 0,
			INVALID_STATE = ScannerStateInfo::INVALID_STATE,
			STATE_COUNT = 128
		};
		
private:
		ScannerStateInfo	        m_stateInfo[STATE_COUNT];
};

//--------------------------------------------------------------------------
// inlines
//--------------------------------------------------------------------------
#ifndef		_SCANNER_STATE_INL_
#include    "scanner_state.inl"
#endif	//	_SCANNER_STATE_INL_

//--------------------------------------------------------------------------

#endif	//	_SCANNER_STATE_H_
