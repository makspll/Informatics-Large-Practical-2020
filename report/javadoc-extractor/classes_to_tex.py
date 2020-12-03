from extract_classes import parse_jdoc
import sys
from pylatex.utils import escape_latex
import re

def clean_string(s):

    s = escape_latex(s)
    s = s.replace(u'\u200b',' ')
    return s

def format_class_tex(c):
    string = ""

    string += "\subparagraph{{ {} }} {}\n \n".format(c.name,c.description)
    
    if c.implements != []:
        for i in c.implements:
            string += "Implements \\textbf{{ {} }}\n".format(clean_string(i))
    string += "\\hrule\n"
    if c.methods != []:
        string+= "\\begin{mitem}\n"
        string+= "\\scriptsize\n"

        for m in c.methods:
            string+= "\t\\mitemxx{{ \\textbf{{ {} }} (".format(clean_string(m.signature))

            for p in m.parameters:
                string+= "{}".format(clean_string(p))
            string += ")}\n"

            string+= "\t{"
            string+=clean_string(m.description)
            string+="}\n"

        string += "\\end{mitem}\n"

    return string


if __name__ == "__main__":
    classes = parse_jdoc(sys.argv[1])

    classes.sort(key=lambda x: x.module, reverse=True)

    for a in sys.argv[1:]:
        for i, c in enumerate(classes):
            if c.name == a:
                del classes[i]
    #TODO: AttributeMap doesnt return the correct name 
    output = open("doc.tex",'w')
    module = None
    prev_module = -1
    for c in classes:
        prev_module = module
        module = c.module

        if module != prev_module:
            output.write("\\subsubsection{{ {} }}\n".format(c.module))

        output.write(format_class_tex(c))
        output.write("\n")


