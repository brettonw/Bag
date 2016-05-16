#ifndef     _XML_SCANNER_H_
#define     _XML_SCANNER_H_

//------------------------------------------------------------------------------
//	includes
//------------------------------------------------------------------------------
#ifndef     _SCANNER_H_
#include    "scanner.h"
#endif  //  _SCANNER_H_

//------------------------------------------------------------------------------
//	class definitions
//------------------------------------------------------------------------------
class   XMLScanner : public Scanner
{
public:
        /* void */              XMLScanner (void);
virtual /* void */              ~XMLScanner (void);
virtual	void                    BuildScanner (void);

public:
        // states
        const enum
        {
            DEFAULT_STATE = ScannerState::DEFAULT_STATE,
            STRING_STATE,
            BEGIN_OPEN_TAG_STATE,
            OPEN_TAG_STATE,
            END_OPEN_TAG_STATE,
            ATTRIBUTE_NAME_STATE,
            ATTRIBUTE_EQ_STATE,
            ATTRIBUTE_SQUOTE_STATE_1,
            ATTRIBUTE_SQUOTE_STATE_2,
            ATTRIBUTE_DQUOTE_STATE_1,
            ATTRIBUTE_DQUOTE_STATE_2,
            CLOSE_TAG_STATE,
            SPECIAL_TAG_STATE,
            EMPTY_TAG_STATE,
            UNUSED_STATE
        };
        
        // actions
        const enum
        {
            OUTPUT_WHITESPACE = 1,  // output whitespace between tags
            OUTPUT_STRING,          // output a string
            OUTPUT_OPEN_TAG,        // output an open tag
            OUTPUT_END_OPEN_TAG,    // output an end to an open tag
            OUTPUT_ATTRIBUTE_NAME,  // output the name of an attribute
            OUTPUT_ATTRIBUTE_EQ,    // output the equal sign between an attribute and its value
            OUTPUT_ATTRIBUTE_VALUE, // output the quoted string value of the attribute
            OUTPUT_CLOSE_TAG,       // output a close tag
            OUTPUT_EMPTY_TAG,       // output an empty tag
            OUTPUT_SPECIAL_TAG,     // output a special tag handled by the xml client (comment, element, entity, ...)
            OUTPUT_ERROR            // output an error
        };
};

//------------------------------------------------------------------------------

#endif  //  _XML_SCANNER_H_
