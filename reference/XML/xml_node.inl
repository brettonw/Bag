#ifndef     _XML_NODE_INL_
#define     _XML_NODE_INL_

//--------------------------------------------------------------------------
// includes
//--------------------------------------------------------------------------
#ifndef     _XML_NODE_H_
#include    "xml_node.h"
#endif  //  _XML_NODE_H_

//--------------------------------------------------------------------------
// class methods
//--------------------------------------------------------------------------
inline
/* void */
XMLNode::XMLNode (const Text& name) : 
    m_name (name)
{
}

//--------------------------------------------------------------------------
inline
/* void */
XMLNode::XMLNode (const Text& name, const Text& value) : 
    m_name (name), 
    m_value (value)
{
}

//--------------------------------------------------------------------------
inline
void
XMLNode::SetName (const Text& name)
{
    m_name = name;
}

//--------------------------------------------------------------------------
inline
Text
XMLNode::GetName (void) const
{
    return m_name;
}

//--------------------------------------------------------------------------
inline
void
XMLNode::SetAttribute (const Text& name, const Text& value)
{
    m_attributes[name] = value;
}

//--------------------------------------------------------------------------
inline
Text
XMLNode::GetAttribute (const Text& name) const
{
    TextHashMap<Text>::const_iterator iterator = m_attributes.find (name);
    if (iterator != m_attributes.end ())
        return iterator->second;
    return Text ();
}

//--------------------------------------------------------------------------
inline
void
XMLNode::SetValue (const Text& value)
{
    m_value = value;
}

//--------------------------------------------------------------------------
inline
Text
XMLNode::GetValue (void) const
{
    return m_value;
}

//--------------------------------------------------------------------------
inline
void
XMLNode::PutChild (const PtrToXMLNode& node)
{
    Text    name = node->GetName ();
    m_children[name].push_back (node);
}

//--------------------------------------------------------------------------
inline
const XMLNodeListMap*
XMLNode::GetAllChildren (void) const
{
    return &m_children;
}

//--------------------------------------------------------------------------

#endif  //  _XML_NODE_INL_
