#ifndef     _XML_NODE_H_
#define     _XML_NODE_H_

//--------------------------------------------------------------------------
// useful type definitions
//--------------------------------------------------------------------------
class   XMLNode;
typedef PtrTo<XMLNode>                  PtrToXMLNode;
typedef std::list<PtrToXMLNode>         XMLNodeList;
typedef XMLNodeList::const_iterator     XMLNodeListIterator;
typedef TextHashMap<XMLNodeList>        XMLNodeListMap;
typedef XMLNodeListMap::const_iterator  XMLNodeListMapIterator;

//--------------------------------------------------------------------------
// class definitions
//--------------------------------------------------------------------------
class XMLNode : public CountedObject
{
    public:
        /* void */                  XMLNode (const Text& name);
        /* void */                  XMLNode (const Text& name, const Text& value);
virtual /* void */                  ~XMLNode (void);

        void                        SetName (const Text& name);
        Text                        GetName (void) const;

        void                        SetAttribute (const Text& name, const Text& value);
        Text                        GetAttribute (const Text& name) const;

        void                        SetValue (const Text& name);
        Text                        GetValue (void) const;
        
        void                        PutChild (const PtrToXMLNode& node);
        PtrToXMLNode                GetChild (const Text& name) const;
        uInt                        GetChildCount (const Text& name) const;
        const XMLNodeList*          GetChildren (const Text& name) const;
        const XMLNodeListMap*       GetAllChildren (void) const;

        
static  Text                        GenerateXML (const PtrToXMLNode& node);
static  PtrToXMLNode                ParseXML (const Text& xml);

static  PtrToXMLNode                FromFile (const Text& fileName);
static  void                        ToFile (const Text& fileName, const PtrToXMLNode& xmlNode);
        
    protected:
        Text                        m_name;
        Text                        m_value;
        TextHashMap<Text>           m_attributes;
        TextHashMap<XMLNodeList>    m_children;
};

//--------------------------------------------------------------------------
// inlines
//--------------------------------------------------------------------------
#ifndef     _XML_NODE_INL_
#include    "xml_node.inl"
#endif  //  _XML_NODE_INL_

//--------------------------------------------------------------------------

#endif  //  _XML_NODE_H_
