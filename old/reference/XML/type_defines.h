#ifndef	_TYPE_DEFINES_H_
#define	_TYPE_DEFINES_H_

//--------------------------------------------------------------------------
//	type definitions
//--------------------------------------------------------------------------
typedef	char			sInt1;												//	a signed integer 1 byte in length
typedef	short			sInt2;												//	a signed integer 2 bytes in length
typedef	long			sInt4;												//	a signed integer 4 bytes in length

typedef	unsigned char	uInt1;												//	an unsigned integer 1 byte in length
typedef	unsigned short	uInt2;												//	an unsigned integer 2 bytes in length
typedef	unsigned long	uInt4;												//	an unsigned integer 4 bytes in length

typedef	sInt4			sInt;												//	signed integer
typedef	uInt4			uInt;												//	unsigned integer

typedef	sInt1			Character;											//	a common character type
typedef	sInt1*			String;												//	a common string type
typedef	const sInt1*	cString;											//	a common string type that is const

typedef	sInt1*			sPointer;											//	standard signed pointer type
typedef	uInt1*			uPointer;											//	standard unsigned pointer type

//--------------------------------------------------------------------------
//	external type limit values
//--------------------------------------------------------------------------
extern	uInt1			FLT_NAN[4];											//	value for not a number
extern	uInt1			FLT_INF[4];											//	value for infinity (x / 0)
extern	uInt1			FLT_NINF[4];										//	value for negative infinity (-x / 0)
extern	uInt1			FLT_IND[4];											//	value for infinity (0 / 0)

extern	uInt1			DBL_NAN[8];											//	value for not a number
extern	uInt1			DBL_INF[8];											//	value for infinity (x / 0)
extern	uInt1			DBL_NINF[8];										//	value for infinity (-x / 0)
extern	uInt1			DBL_IND[8];											//	value for infinity (0 / 0)

//--------------------------------------------------------------------------
//	type limits
//--------------------------------------------------------------------------
#define	SINT1_MIN		CHAR_MIN											//	minimum value
#define	SINT1_MAX		CHAR_MAX											//	maximum value
#define	SINT2_MIN		SHRT_MIN											//	minimum value
#define	SINT2_MAX		SHRT_MAX											//	maximum value
#define	SINT4_MIN		LONG_MIN											//	minimum value
#define	SINT4_MAX		LONG_MAX											//	maximum value

#define	UINT1_MIN		0													//	minimum value
#define	UINT1_MAX		UCHAR_MAX											//	maximum value
#define	UINT2_MIN		0													//	minimum value
#define	UINT2_MAX		USHRT_MAX											//	maximum value
#define	UINT4_MIN		0													//	minimum value
#define	UINT4_MAX		ULONG_MAX											//	maximum value

#define	SINT_MIN		SINT4_MIN											//	minimum value
#define	SINT_MAX		SINT4_MAX											//	maximum value

#undef	UINT_MAX
#define	UINT_MIN		UINT4_MIN											//	minimum value
#define	UINT_MAX		UINT4_MAX											//	maximum value

//--------------------------------------------------------------------------

#endif // _TYPE_DEFINES
