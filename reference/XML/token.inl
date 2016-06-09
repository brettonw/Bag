#ifndef		_TOKEN_INL_
#define		_TOKEN_INL_

//------------------------------------------------------------------------------
//	includes
//------------------------------------------------------------------------------
#ifndef		_TOKEN_H_
#include    "token.h"
#endif	//  _TOKEN_H_

//------------------------------------------------------------------------------
//	class methods
//------------------------------------------------------------------------------
inline
/* void */
Token::Token (void)
{
}

//------------------------------------------------------------------------------
inline
/* void */
Token::Token (cString buffer, uInt length, uInt2 type) : 
    m_buffer (buffer), 
    m_length (length),
    m_type (type)
{
}

//------------------------------------------------------------------------------
inline
void
Token::SetText (cString buffer, uInt length)
{
    m_buffer = buffer;
    m_length = length;
}

//------------------------------------------------------------------------------
inline
Text
Token::GetText (void) const
{
    return Text (m_buffer, m_length);
}

//------------------------------------------------------------------------------
inline
void
Token::SetType (uInt2 type)
{
    m_type = type;
}

//------------------------------------------------------------------------------
inline
uInt2
Token::GetType (void) const
{
    return m_type;
}

//------------------------------------------------------------------------------

#endif	//  _TOKEN_INL_
