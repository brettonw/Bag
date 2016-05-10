#ifndef     _SCANNER_H_
#define     _SCANNER_H_

//--------------------------------------------------------------------------
// includes
//--------------------------------------------------------------------------
#ifndef     _SCANNER_STATE_H_
#include    "scanner_state.h"
#endif  //  _SCANNER_STATE_H_

#ifndef     _SCANNER_ACTION_H_
#include    "scanner_action.h"
#endif  //  _SCANNER_ACTION_H_

//--------------------------------------------------------------------------
// class definitions
//--------------------------------------------------------------------------
class Scanner
{
public:
        /* void */                  Scanner (uInt stateCount);

        // methods for building scanner state table
virtual void                        BuildScanner (void) = 0;
        void                        ResetScanner (void);
        void                        OnInput (uInt2 state, cString input, uInt2 nextState, uInt2 storage, uInt2 action);
        void                        OnAnyInput (uInt2 state, uInt2 nextState, uInt2 storage, uInt2 action);
        void                        OnAlphabeticInput (uInt2 state, uInt2 nextState, uInt2 storage, uInt2 action);
        void                        OnNumericInput (uInt2 state, uInt2 nextState, uInt2 storage, uInt2 action);
        void                        OnHexadecimalInput (uInt2 state, uInt2 nextState, uInt2 storage, uInt2 action);
        void                        OnWhitespaceInput (uInt2 state, uInt2 nextState, uInt2 storage, uInt2 action);
        
        // methods for performing the scan operation
        uInt2                       ScanText (cString text, uInt length, ScannerAction* action, uInt2 startState) const;
        uInt2                       ScanCharacter (cString& text, ScannerAction* action, uInt2 state) const;
        uInt2                       EndScan (cString text, ScannerAction* action, uInt2 state) const;
        
public:
        //	input control codes
        const enum
        {
            DONT_STORE_INPUT = 0,       // do not store the input
            STORE_INPUT_BEFORE = 1,     // store the input into the buffer before performing the action
            STORE_INPUT_AFTER = 2       // store the input into the buffer after performing the action
        };

        // actions        
		const enum
		{
			NO_ACTION = 0,				// perform no action on an input state
		};

protected:
        std::vector<ScannerState>   m_stateTable;
mutable cString                     m_start;
mutable cString                     m_stop;
};

//--------------------------------------------------------------------------

#endif	//	_SCANNER_H_
