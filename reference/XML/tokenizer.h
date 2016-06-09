#ifndef		_TOKENIZER_H_
#define		_TOKENIZER_H_

//--------------------------------------------------------------------------
//	includes
//--------------------------------------------------------------------------
#ifndef		_TOKEN_H_
#include	"token.h"
#endif	//	_TOKEN_H_

#ifndef		_SCANNER_H_
#include	"scanner.h"
#endif	//	_SCANNER_H_

//--------------------------------------------------------------------------
// useful type definitions
//--------------------------------------------------------------------------
typedef std::vector<Token>      TokenVector;

//--------------------------------------------------------------------------
class TokenizedText
{
public:
        /* void */          TokenizedText (const Text& text)
                            { m_source = text; }

public:
        Text                m_source;
        TokenVector         m_tokens;
};

//--------------------------------------------------------------------------
//	class definitions
//--------------------------------------------------------------------------
class   Tokenizer : public ScannerAction
{
public:
		/* void */          Tokenizer (const Scanner* pScanner);
virtual	/* void */          ~Tokenizer (void);
virtual	void				Action (cString buffer, uInt length, uInt2 action, cString nextBuffer, uInt2 nextState);
        uInt2               Tokenize (TokenizedText* pTokenizedText, uInt2 startState = ScannerState::DEFAULT_STATE);
        
protected:
	    const Scanner*      m_pScanner;
	    TokenizedText*      m_pTokenizedText;
};

//--------------------------------------------------------------------------

#endif	//  _TOKENIZER_H_
