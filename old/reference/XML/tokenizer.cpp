//------------------------------------------------------------------------------
//	includes
//------------------------------------------------------------------------------
#include    "pch_include.h"
#include    "tokenizer.h"

//------------------------------------------------------------------------------
//	class methods
//------------------------------------------------------------------------------
/* void */
Tokenizer::Tokenizer (const Scanner* pScanner) : 
    m_pScanner (pScanner),
    m_pTokenizedText (0)
{
}

//------------------------------------------------------------------------------
/* void */
Tokenizer::~Tokenizer (void)
{
}

//------------------------------------------------------------------------------
void
Tokenizer::Action (cString buffer, uInt length, uInt2 action, cString nextBuffer, uInt2 nextState)
{
    // check that this is a valid call
    assert (m_pTokenizedText);
    
    // add the found values to the tail of the token vector
    m_pTokenizedText->m_tokens.push_back (Token (buffer, length, action));
}

//------------------------------------------------------------------------------
uInt2
Tokenizer::Tokenize (TokenizedText* pTokenizedText, uInt2 startState)
{
    // store the address of the tokenized text structure for later reference
    m_pTokenizedText = pTokenizedText;
    
    // scan the text stream converting it into tokens
    uInt2   endState = m_pScanner->ScanText (static_cast<cString> (pTokenizedText->m_source), pTokenizedText->m_source.Length (), this, startState);
    
    // clear the address of the tokenized text structure for safety
    m_pTokenizedText = 0;
    
    // return the end state
    return endState;
}

//------------------------------------------------------------------------------
