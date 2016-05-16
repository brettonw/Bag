//--------------------------------------------------------------------------
//	includes
//--------------------------------------------------------------------------
#include    "pch_include.h"
#include    "bits.h"
#include    "auto_array_ptr_to.h"

//--------------------------------------------------------------------------
//	global variables
//--------------------------------------------------------------------------
AutoArrayPtrTo<sInt1>     gBitCount;

//--------------------------------------------------------------------------
//	functions
//--------------------------------------------------------------------------
sInt
CountBits
(
	sInt4				value
)
{
    if (not gBitCount.GetPtr ())
    {
	gBitCount = new sInt1[256];
	for (sInt i = 0; i < 256; i++)
	{
		gBitCount[i] = 0;
		for (sInt bits = i; bits != 0; gBitCount[i]++)
			bits &= bits - 1;
	}
    }
    uInt1*	ptr = (uInt1*) &value;
    return gBitCount[ptr[0]] + gBitCount[ptr[1]] + gBitCount[ptr[2]] + gBitCount[ptr[3]];
}

//--------------------------------------------------------------------------
sInt
HighestBitPosition
(
	sInt4				value
)
{
	sInt	position = 0;
	while (value)
	{
		value >>= 1;
		position++;
	}
	return position;
}

//--------------------------------------------------------------------------
sInt4
HighestBit
(
	sInt4				value
)
{
	return 1 << (HighestBitPosition (value) - 1);
}

//--------------------------------------------------------------------------
sInt
LowestBitPosition
(
	sInt4				value
)
{
	return HighestBitPosition (LowestBit (value));
}

//--------------------------------------------------------------------------
sInt4
LowestBit
(
	sInt4				value
)
{
	return value bitand -value;
}

//--------------------------------------------------------------------------
bool
IsPowerOf2
(
	sInt4				value
)
{
	return bool (value == LowestBit (value));
}

//--------------------------------------------------------------------------
void
PrintBits
(
	uInt4				value
)
{
	for (uInt j = 0; j < 3; j++)
	{
		for (uInt i = 0; i < 8; i++)
		{
			STD_DECL(cerr) << ((value bitand 0x80000000) ? "1" : "0");
			value <<= 1;
		}
		STD_DECL(cerr) << ".";
	}
	for (uInt i = 0; i < 8; i++)
	{
		STD_DECL(cerr) << ((value bitand 0x80000000) ? "1" : "0");
		value <<= 1;
	}
}

//--------------------------------------------------------------------------
