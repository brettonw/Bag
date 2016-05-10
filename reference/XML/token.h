#ifndef		_TOKEN_H_
#define		_TOKEN_H_

//------------------------------------------------------------------------------
//	includes
//------------------------------------------------------------------------------
#ifndef		_TEXT_H_
#include	"text.h"
#endif	//	_TEXT_H_

//--------------------------------------------------------------------------
// useful type definitions
//--------------------------------------------------------------------------
class   Token;
typedef PtrTo<Token>                  PtrToToken;

//------------------------------------------------------------------------------
//	class definitions
//------------------------------------------------------------------------------
class	Token
{
public:
		/* void */  Token (void);
		/* void */  Token (cString buffer, uInt length, uInt2 type);

        void        SetText (cString buffer, uInt length);
        Text        GetText (void) const;
        
        void        SetType (uInt2 type);
        uInt2       GetType (void) const;

protected:
	    cString     m_buffer;
	    uInt        m_length;
	    uInt2       m_type;
};

//------------------------------------------------------------------------------
//	inlines
//------------------------------------------------------------------------------
#ifndef		_TOKEN_INL_
#include	"token.inl"
#endif	//	_TOKEN_INL_

//------------------------------------------------------------------------------

#endif	//  _TOKEN_H_
