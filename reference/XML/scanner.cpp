//------------------------------------------------------------------------------
//	includes
//------------------------------------------------------------------------------
#include    "pch_include.h"
#include    "scanner.h"

//------------------------------------------------------------------------------
//	class methods
//------------------------------------------------------------------------------
/* void */
Scanner::Scanner (uInt stateCount) :
    m_stateTable (stateCount)
{
}

//------------------------------------------------------------------------------
void
Scanner::ResetScanner (void)
{
    uInt    capacity = m_stateTable.capacity ();
    for (uInt i = 0; i < capacity; i++)
        m_stateTable[i].SetAll (ScannerState::INVALID_STATE, DONT_STORE_INPUT, NO_ACTION);
}

//------------------------------------------------------------------------------
void
Scanner::OnInput (uInt2 state, cString input, uInt2 nextState, uInt2 storage, uInt2 action)
{
	assert (not ((nextState == state) and (storage == STORE_INPUT_AFTER)));
	if (strlen (input) > 0)
		while (*input)
			m_stateTable[state].Set (*input++, nextState, storage, action);
	else
		m_stateTable[state].Set (0, nextState, storage, action);
}

//------------------------------------------------------------------------------
void
Scanner::OnAnyInput (uInt2 state, uInt2 nextState, uInt2 storage, uInt2 action)
{
	assert (not ((nextState == state) and (storage == STORE_INPUT_AFTER)));
	m_stateTable[state].SetAll (nextState, storage, action);
}

//------------------------------------------------------------------------------
void
Scanner::OnAlphabeticInput (uInt2 state, uInt2 nextState, uInt2 storage, uInt2 action)
{
	assert (not ((nextState == state) and (storage == STORE_INPUT_AFTER)));
	for (uInt2 i = 0; i < ScannerState::STATE_COUNT; i++)
		if (isalnum (i))
			m_stateTable[state].Set (i, nextState, storage, action);
}

//------------------------------------------------------------------------------
void
Scanner::OnNumericInput (uInt2 state, uInt2 nextState, uInt2 storage, uInt2 action)
{
	assert (not ((nextState == state) and (storage == STORE_INPUT_AFTER)));
	for (uInt2 i = 0; i < ScannerState::STATE_COUNT; i++)
		if (isdigit (i))
			m_stateTable[state].Set (i, nextState, storage, action);
}

//------------------------------------------------------------------------------
void
Scanner::OnHexadecimalInput (uInt2 state, uInt2 nextState, uInt2 storage, uInt2 action)
{
	assert (not ((nextState == state) and (storage == STORE_INPUT_AFTER)));
	for (uInt2 i = 0; i < ScannerState::STATE_COUNT; i++)
		if (isxdigit (i))
			m_stateTable[state].Set (i, nextState, storage, action);
}

//------------------------------------------------------------------------------
void
Scanner::OnWhitespaceInput (uInt2 state, uInt2 nextState, uInt2 storage, uInt2 action)
{
	assert (not ((nextState == state) and (storage == STORE_INPUT_AFTER)));
	for (uInt2 i = 0; i < ScannerState::STATE_COUNT; i++)
		if (isspace (i))
			m_stateTable[state].Set (i, nextState, storage, action);
}

//------------------------------------------------------------------------------
uInt2
Scanner::ScanText (cString text, uInt length, ScannerAction* action, uInt2 startState) const
{
	m_start = m_stop = text;
	cString		stop = text + length;
	while (text != stop)
		startState = ScanCharacter (text, action, startState);
	return EndScan (text, action, startState);
}

//------------------------------------------------------------------------------
uInt2
Scanner::ScanCharacter (cString& text, ScannerAction* action, uInt2 state) const
{
    const ScannerState&     scannerState = m_stateTable[state];
	const ScannerStateInfo&	info = scannerState[*text];
	assert (info.m_nextState != ScannerState::INVALID_STATE);
	state = info.m_nextState;
	switch (info.m_storage)
	{
		case DONT_STORE_INPUT:
			text++;
			break;
		case STORE_INPUT_BEFORE:
			assert (m_stop == text);
			text++;
			m_stop++;
			break;
		case STORE_INPUT_AFTER:
			break;
	}
	if (info.m_action != NO_ACTION)
	{
		action->Action (m_start, m_stop - m_start, info.m_action, text, state);
		m_start = m_stop = text;
	}
	return state;
}

//------------------------------------------------------------------------------
uInt2
Scanner::EndScan (cString text, ScannerAction* action, uInt2 state) const
{
	const	ScannerStateInfo*	info = &m_stateTable[state][0];
	assert (info->m_nextState != ScannerState::INVALID_STATE);

	while (m_start != text)
	{
		if (info->m_action != NO_ACTION)
		{
			action->Action (m_start, m_stop - m_start, info->m_action, text, info->m_nextState);
			m_start = m_stop = text;
		}
		else
		{
			state = info->m_nextState;
			info = &m_stateTable[state][0];
		}
	}
	return info->m_nextState;
}

//------------------------------------------------------------------------------
