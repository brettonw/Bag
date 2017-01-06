//------------------------------------------------------------------------------
//	includes
//------------------------------------------------------------------------------
#include    "pch_include.h"
#include    "xml_scanner.h"

//------------------------------------------------------------------------------
//	class methods
//------------------------------------------------------------------------------
/* void */
XMLScanner::XMLScanner (void) :
    Scanner (UNUSED_STATE)
{
    BuildScanner ();
}

//------------------------------------------------------------------------------
/* void */
XMLScanner::~XMLScanner (void)
{
}

//------------------------------------------------------------------------------
void
XMLScanner::BuildScanner (void)
{
    ResetScanner ();
    
    OnInput             (DEFAULT_STATE,             "\0",   DEFAULT_STATE,              DONT_STORE_INPUT,       NO_ACTION                 );
    OnInput             (DEFAULT_STATE,             "<",    BEGIN_OPEN_TAG_STATE,       STORE_INPUT_BEFORE,     NO_ACTION                 );
    OnWhitespaceInput   (DEFAULT_STATE,                     DEFAULT_STATE,              STORE_INPUT_BEFORE,     OUTPUT_WHITESPACE         );
    OnAnyInput          (DEFAULT_STATE,                     STRING_STATE,               STORE_INPUT_BEFORE,     NO_ACTION                 );
    
    OnInput             (STRING_STATE,              "\0",   DEFAULT_STATE,              DONT_STORE_INPUT,       OUTPUT_ERROR              );
    OnInput             (STRING_STATE,              "<",    DEFAULT_STATE,              STORE_INPUT_AFTER,      OUTPUT_STRING             );
    OnAnyInput          (STRING_STATE,                      STRING_STATE,               STORE_INPUT_BEFORE,     NO_ACTION                 );

    OnInput             (BEGIN_OPEN_TAG_STATE,      "\0",   DEFAULT_STATE,              DONT_STORE_INPUT,       OUTPUT_ERROR              );
    OnInput             (BEGIN_OPEN_TAG_STATE,      "/",    CLOSE_TAG_STATE,            STORE_INPUT_BEFORE,     NO_ACTION                 );
    OnInput             (BEGIN_OPEN_TAG_STATE,      "!",    SPECIAL_TAG_STATE,          STORE_INPUT_BEFORE,     NO_ACTION                 );
    OnInput             (BEGIN_OPEN_TAG_STATE,      "?",    SPECIAL_TAG_STATE,          STORE_INPUT_BEFORE,     NO_ACTION                 );
    OnAnyInput          (BEGIN_OPEN_TAG_STATE,              OPEN_TAG_STATE,             STORE_INPUT_BEFORE,     NO_ACTION                 );

    OnInput             (OPEN_TAG_STATE,            "\0",   DEFAULT_STATE,              DONT_STORE_INPUT,       OUTPUT_ERROR              );
    OnInput             (OPEN_TAG_STATE,            "/",    END_OPEN_TAG_STATE,         STORE_INPUT_AFTER,      OUTPUT_OPEN_TAG           );
    OnInput             (OPEN_TAG_STATE,            ">",    END_OPEN_TAG_STATE,         STORE_INPUT_AFTER,      OUTPUT_OPEN_TAG           );
    OnWhitespaceInput   (OPEN_TAG_STATE,                    END_OPEN_TAG_STATE,         STORE_INPUT_AFTER,      OUTPUT_OPEN_TAG           );
    OnAnyInput          (OPEN_TAG_STATE,                    OPEN_TAG_STATE,             STORE_INPUT_BEFORE,     NO_ACTION                 );

    OnInput             (END_OPEN_TAG_STATE,        "\0",   DEFAULT_STATE,              DONT_STORE_INPUT,       OUTPUT_ERROR              );
    OnInput             (END_OPEN_TAG_STATE,        "/",    EMPTY_TAG_STATE,            STORE_INPUT_BEFORE,     NO_ACTION                 );
    OnInput             (END_OPEN_TAG_STATE,        ">",    DEFAULT_STATE,              STORE_INPUT_BEFORE,     OUTPUT_END_OPEN_TAG       );
    OnWhitespaceInput   (END_OPEN_TAG_STATE,                END_OPEN_TAG_STATE,         STORE_INPUT_BEFORE,     OUTPUT_WHITESPACE         );
    OnAnyInput          (END_OPEN_TAG_STATE,                ATTRIBUTE_NAME_STATE,       STORE_INPUT_BEFORE,     NO_ACTION                 );

    OnInput             (CLOSE_TAG_STATE,           "\0",   DEFAULT_STATE,              DONT_STORE_INPUT,       OUTPUT_ERROR              );
    OnInput             (CLOSE_TAG_STATE,           ">",    DEFAULT_STATE,              STORE_INPUT_BEFORE,     OUTPUT_CLOSE_TAG          );
    OnAnyInput          (CLOSE_TAG_STATE,                   CLOSE_TAG_STATE,            STORE_INPUT_BEFORE,     NO_ACTION                 );

    OnInput             (SPECIAL_TAG_STATE,         "\0",   DEFAULT_STATE,              DONT_STORE_INPUT,       OUTPUT_ERROR              );
    OnInput             (SPECIAL_TAG_STATE,         ">",    DEFAULT_STATE,              STORE_INPUT_BEFORE,     OUTPUT_SPECIAL_TAG        );
    OnAnyInput          (SPECIAL_TAG_STATE,                 SPECIAL_TAG_STATE,          STORE_INPUT_BEFORE,     NO_ACTION                 );

    OnInput             (EMPTY_TAG_STATE,           "\0",   DEFAULT_STATE,              DONT_STORE_INPUT,       OUTPUT_ERROR              );
    OnInput             (EMPTY_TAG_STATE,           ">",    DEFAULT_STATE,              STORE_INPUT_BEFORE,     OUTPUT_EMPTY_TAG          );
    OnAnyInput          (EMPTY_TAG_STATE,                   END_OPEN_TAG_STATE,         STORE_INPUT_BEFORE,     NO_ACTION                 );

    OnInput             (ATTRIBUTE_NAME_STATE,      "\0",   DEFAULT_STATE,              DONT_STORE_INPUT,       OUTPUT_ERROR              );
    OnInput             (ATTRIBUTE_NAME_STATE,      ">",    END_OPEN_TAG_STATE,         STORE_INPUT_AFTER,      OUTPUT_ATTRIBUTE_NAME     );
    OnInput             (ATTRIBUTE_NAME_STATE,      "=",    ATTRIBUTE_EQ_STATE,         STORE_INPUT_AFTER,      OUTPUT_ATTRIBUTE_NAME     );
    OnWhitespaceInput   (ATTRIBUTE_NAME_STATE,              ATTRIBUTE_EQ_STATE,         STORE_INPUT_AFTER,      OUTPUT_ATTRIBUTE_NAME     );
    OnAnyInput          (ATTRIBUTE_NAME_STATE,              ATTRIBUTE_NAME_STATE,       STORE_INPUT_BEFORE,     NO_ACTION                 );

    OnInput             (ATTRIBUTE_EQ_STATE,        "\0",   DEFAULT_STATE,              DONT_STORE_INPUT,       OUTPUT_ERROR              );
    OnInput             (ATTRIBUTE_EQ_STATE,        ">",    END_OPEN_TAG_STATE,         STORE_INPUT_AFTER,      OUTPUT_ERROR              );
    OnInput             (ATTRIBUTE_EQ_STATE,        "=",    ATTRIBUTE_EQ_STATE,         STORE_INPUT_BEFORE,     NO_ACTION                 );
    OnInput             (ATTRIBUTE_EQ_STATE,        "\'",   ATTRIBUTE_SQUOTE_STATE_1,   STORE_INPUT_AFTER,      OUTPUT_ATTRIBUTE_EQ       );
    OnInput             (ATTRIBUTE_EQ_STATE,        "\"",   ATTRIBUTE_DQUOTE_STATE_1,   STORE_INPUT_AFTER,      OUTPUT_ATTRIBUTE_EQ       );
    OnWhitespaceInput   (ATTRIBUTE_EQ_STATE,                ATTRIBUTE_EQ_STATE,         STORE_INPUT_BEFORE,     NO_ACTION                 );
    OnAnyInput          (ATTRIBUTE_EQ_STATE,                ATTRIBUTE_EQ_STATE,         STORE_INPUT_BEFORE,     OUTPUT_ERROR              );

    OnAnyInput          (ATTRIBUTE_SQUOTE_STATE_1,          ATTRIBUTE_SQUOTE_STATE_2,   STORE_INPUT_BEFORE,     NO_ACTION                 );

    OnInput             (ATTRIBUTE_SQUOTE_STATE_2,  "\0",   DEFAULT_STATE,              DONT_STORE_INPUT,       OUTPUT_ERROR              );
    OnInput             (ATTRIBUTE_SQUOTE_STATE_2,  "\'",   END_OPEN_TAG_STATE,         STORE_INPUT_BEFORE,     OUTPUT_ATTRIBUTE_VALUE    );
    OnAnyInput          (ATTRIBUTE_SQUOTE_STATE_2,          ATTRIBUTE_SQUOTE_STATE_2,   STORE_INPUT_BEFORE,     NO_ACTION                 );

    OnAnyInput          (ATTRIBUTE_DQUOTE_STATE_1,          ATTRIBUTE_DQUOTE_STATE_2,   STORE_INPUT_BEFORE,     NO_ACTION                 );

    OnInput             (ATTRIBUTE_DQUOTE_STATE_2,  "\0",   DEFAULT_STATE,              DONT_STORE_INPUT,       OUTPUT_ERROR              );
    OnInput             (ATTRIBUTE_DQUOTE_STATE_2,  "\"",   END_OPEN_TAG_STATE,         STORE_INPUT_BEFORE,     OUTPUT_ATTRIBUTE_VALUE    );
    OnAnyInput          (ATTRIBUTE_DQUOTE_STATE_2,          ATTRIBUTE_DQUOTE_STATE_2,   STORE_INPUT_BEFORE,     NO_ACTION                 );
}

//------------------------------------------------------------------------------
