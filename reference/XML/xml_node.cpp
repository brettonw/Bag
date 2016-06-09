//--------------------------------------------------------------------------
// includes
//--------------------------------------------------------------------------
#include    "pch_include.h"
#include    "xml_node.h"
#include    "xml_scanner.h"
#include    "tokenizer.h"

//--------------------------------------------------------------------------
// class methods
//--------------------------------------------------------------------------
/* void */
XMLNode::~XMLNode (void)
{
}

//--------------------------------------------------------------------------
PtrToXMLNode
XMLNode::GetChild (const Text& name) const
{
    XMLNodeListMapIterator  foundList = m_children.find (name);
    if (foundList != m_children.end ())
    {
        XMLNodeList::const_iterator childIterator = foundList->second.begin ();
        if (childIterator != foundList->second.end ())
            return *childIterator;
    }
    return 0;
}

//--------------------------------------------------------------------------
uInt
XMLNode::GetChildCount (const Text& name) const
{
    XMLNodeListMapIterator  foundList = m_children.find (name);
    if (foundList != m_children.end ())
        return foundList->second.size ();
    return 0;
}

//--------------------------------------------------------------------------
const XMLNodeList*
XMLNode::GetChildren (const Text& name) const
{
    XMLNodeListMapIterator  foundList = m_children.find (name);
    if (foundList != m_children.end ())
        return &(foundList->second);
    return 0;
}

//--------------------------------------------------------------------------
Text
XMLNode::GenerateXML (const PtrToXMLNode& node)
{
    // start with the value text
    Text                                xml = node->m_value;

    // add in all the children
    XMLNodeListMapIterator              childListIterator = node->m_children.begin ();
    while (childListIterator != node->m_children.end ())
    {
        // loop over all the nodes in the child list
        XMLNodeList::const_iterator     nodeIterator = childListIterator->second.begin ();
        while (nodeIterator != childListIterator->second.end ())
        {
            // include the child node xml in ours
            xml += GenerateXML (*nodeIterator);
            
            // advance to the next node
            ++nodeIterator;
        }
        
        // advance to the next list in the table
        ++childListIterator;
    }
    
    // build the tag string
    Text                                tag = node->m_name;
    TextHashMap<Text>::const_iterator   attributeIterator = node->m_attributes.begin ();
    while (attributeIterator != node->m_attributes.end ())
    {
        tag = tag + " " + attributeIterator->first + "=\"" + attributeIterator->second + "\"";
        ++attributeIterator;
    }

    // build the xml tag the right way, depending on whether or not it's empty
    if (xml.Length () > 0)
        return Text ("<") + tag + ">" + xml + "</" + node->m_name + ">\n";
    else
        return Text ("<") + tag + "/>\n";
}

//--------------------------------------------------------------------------
// forward declaration
PtrToXMLNode BuildXMLNode (TokenVector::iterator& tokenIterator, TokenVector::iterator& end);

//--------------------------------------------------------------------------
void
BuildXMLNodeChildren (PtrToXMLNode& node, TokenVector::iterator& tokenIterator, TokenVector::iterator& end)
{
    // loop until a close tag is found (it should match the node name)
    while (tokenIterator != end)
    {
        // advance to the next token
        ++tokenIterator;

        // get the current item
        const Token&    token = *tokenIterator;
                        
        // handle the token appropriately
        switch (token.GetType ())
        {
            case XMLScanner::OUTPUT_STRING:
                {
                    // append this to the current value
                    node->SetValue (node->GetValue () + token.GetText ());
                }
                break;
                
            case XMLScanner::OUTPUT_OPEN_TAG:
            case XMLScanner::OUTPUT_SPECIAL_TAG:
                {
                    // this is a child node, build it and put it in the child table
                    PtrToXMLNode    childNode = BuildXMLNode (tokenIterator, end);
                    if (childNode)
                        node->PutChild (childNode);
                }
                break;

            case XMLScanner::OUTPUT_ERROR:
                // an error was encountered in the file
                // skip it and hope everything works out
                break;

            case XMLScanner::OUTPUT_CLOSE_TAG:
                // check that the name matches the node name
                //assert (token->GetText () == node->GetName ());
                
                // we're finished here
                return;
        }
    }
}

//--------------------------------------------------------------------------
PtrToXMLNode
BuildXMLNode (TokenVector::iterator& tokenIterator, TokenVector::iterator& end)
{
    // first off, we expect that the iterator is not done
    if (tokenIterator != end)
    {
        // get the current token
        const Token&    token = *tokenIterator;
        
        // this should be OUTPUT_OPEN_TAG or OUTPUT_SPECIAL_TAG
        switch (token.GetType ())
        {
            case XMLScanner::OUTPUT_OPEN_TAG:
                {
                    // extract the name
                    Text            tokenText = token.GetText ();
                    Text            name = tokenText.GetSubtext (1, tokenText.Length () - 1);
                    
                    // build the xml node
                    PtrToXMLNode    node = new XMLNode (name);
                    
                    // set up the current attribute name
                    Text            attributeName;
                    
                    // loop until OUTPUT_END_OPEN_TAG or OUTPUT_EMPTY_TAG is encountered
                    while (tokenIterator != end)
                    {
                        // advance the token
                        ++tokenIterator;

                        // get the current item
                        const Token&    token = *tokenIterator;
                        
                        // handle the attributes and child construction
                        switch (token.GetType ())
                        {
                            case XMLScanner::OUTPUT_ATTRIBUTE_NAME:
                                // set the attribute
                                attributeName = token.GetText ();
                                break;
                                
                            case XMLScanner::OUTPUT_ATTRIBUTE_VALUE:
                                // check to see if the attribute is valid
                                if (attributeName.Length () > 0)
                                {
                                    // get the unquoted part of the text
                                    Text    tokenText = token.GetText ();
                                    Text    attributeValue = tokenText.GetSubtext (1, tokenText.Length () - 2);
                                    
                                    // install the attribute
                                    node->SetAttribute (attributeName, attributeValue);
                                    
                                    // clear the attribute name
                                    attributeName = Text ();
                                }
                                break;
                            
                            case XMLScanner::OUTPUT_END_OPEN_TAG:
                                // build the children and advance to the close tag
                                BuildXMLNodeChildren (node, tokenIterator, end);
                                
                                // fall through
                            
                            case XMLScanner::OUTPUT_EMPTY_TAG:
                                // advance the token
                                //tokenIterator->GotoNextItem ();
                                
                                // return the built node
                                return node;
                        }
                    }
                }
                
            case XMLScanner::OUTPUT_SPECIAL_TAG:
                // ignore this tag. We could do some processing on it, but I don't
                // know what we care about it for
                break;
        }
    }
    
    // The token iterator was done, the string contained no nodes, or there was 
    // some other error. We return what we've got so far, which is nothing.
    return 0;
}

//--------------------------------------------------------------------------
PtrToXMLNode
XMLNode::ParseXML (const Text& xml)
{
    // tokenize the xml
    XMLScanner      xmlScanner;
    TokenizedText   tokenizedText (xml);
    Tokenizer       tokenizer (&xmlScanner);
    tokenizer.Tokenize (&tokenizedText);

    // build the nodes recursively and return what comes back
    return BuildXMLNode (tokenizedText.m_tokens.begin (), tokenizedText.m_tokens.end ());
}

//--------------------------------------------------------------------------
PtrToXMLNode
XMLNode::FromFile (const Text& fileName)
{
    Text    xmlText = Text::FromFile (fileName);
    if (xmlText.Length () > 0)
        return ParseXML (xmlText);
    return 0;
}

//--------------------------------------------------------------------------
void
XMLNode::ToFile (const Text& fileName, const PtrToXMLNode& xmlNode)
{        
    Text    xmlText = GenerateXML (xmlNode);
    Text::ToFile (fileName, xmlText);
}

//--------------------------------------------------------------------------
