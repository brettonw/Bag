package lib.frost.bag;

// The BagParser is loosely modeled after a JSON parser grammar from the site (http://www.json.org).
// The main difference is that we only expect to parse a string generated from a ToString call on a
// BagObject or BagArray, so we ignore differences between value types (all of them will be strings
// internally), don't handle extraneous whitespace, and assume the input is a well formed string
// representation of a BagObject or BagArray

public class BagParser {
    private int index;
    private String input;

    public BagParser(String input)
    {
        this.input = input;
        index = 0;
    }

    public BagArray ReadBagArray()
    {
        // <Array> :: [ ] | [ <Elements> ]
        BagArray bagArray = new BagArray();
        return (Expect('[') && ReadElements(bagArray) && Expect(']')) ? bagArray : null;
    }

    public BagObject ReadBagObject()
    {
        // <Object> ::= { } | { <Members> }
        BagObject bagObject = new BagObject();
        return (Expect('{') && ReadMembers(bagObject) && Expect('}')) ? bagObject : null;
    }

    private boolean Expect(char c)
    {
        if (input.charAt (index) == c)
        {
            ++index;
            return true;
        }
        return false;
    }

    private boolean ReadElements(BagArray bagArray)
    {
        // <Elements> ::= <Value> | <Value> , <Elements>
        bagArray.add (ReadValue());
        return (Expect(',') && ReadElements(bagArray)) || true;
    }

    private boolean ReadMembers(BagObject bagObject)
    {
        // <Members> ::= <Pair> | <Pair> , <Members>
        return ReadPair(bagObject) && ((Expect(',') && ReadMembers(bagObject)) || true);
    }

    private boolean ReadPair(BagObject bagObject)
    {
        // <Pair> ::= <String> : <Value>
        String key = ReadString();
        if ((key.length () > 0) && Expect(':'))
        {
            Object value = ReadValue();
            if (value != null)
            {
                bagObject.put (key, value);
                return true;
            }
        }
        return false;
    }

    private String ReadString()
    {
        // read a string that allows quoted strings internally
        String result = null;
        if (Expect('"'))
        {
            StringBuilder stringBuilder = new StringBuilder();
            boolean isDone = false;
            while (!isDone)
            {
                char c = input.charAt (index);
                switch (c)
                {
                    case '\\':
                        stringBuilder.append (input.charAt (++index));
                        break;

                    case '"':
                        isDone = true;
                        break;

                    default:
                        stringBuilder.append (c);
                        break;
                }
                ++index;
            }
            result = stringBuilder.toString ();
        }
        return result;
    }

    private Object ReadValue()
    {
        // <Value> ::= <String> | <Object> | <Array>
        Object value = null;
        switch (input.charAt (index))
        {
            case '"':
                value = ReadString();
                break;

            case '{':
                value = ReadBagObject();
                break;

            case '[':
                value = ReadBagArray();
                break;
        }
        return value;
    }
}
