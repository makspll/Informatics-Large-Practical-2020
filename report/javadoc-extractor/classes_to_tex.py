from extract_classes import parse_jdoc
import sys
from pylatex.utils import escape_latex
import re

def clean_string(s):

    s = escape_latex(s)
    s = s.replace(u'\u200b',' ')
    s = s.replace('<',"$<$")
    s = s.replace('>',"$>$")
    return s

def format_class_tex(c):
    string = ""

    string += "\cbox{{ {} {} }} {{ {}\n \n".format(clean_string(c.c_type),clean_string(c.name),clean_string(c.description))
    
    if c.implements != []:
        string += "\\vspace*{4pt} \\hrule \\vspace*{3pt}\n"
        for i in c.implements:
            string += "Implements \\textbf{{ {} }}\n".format(clean_string(i))

    if len(c.methods) > 0:
        string+= "\\tcbsubtitle[before skip=\\baselineskip]{Members}"

    if c.methods != []:
        string+= "\\begin{tabular}{ p{3in}|m{3.4in}}\n"
        for m in c.methods:
            string += format_method(m)
        string += "\\end{tabular}\n"
    
    string += "}"

    return string

def format_method(m):
    # cell 1
    string = ""
    string+= "\\textbf{{{}}} (".format(clean_string(m.signature))
    ixd = len(m.parameters)

    if len(m.parameters) > 0:
        string += "\\\\ "
        pass

    for p in m.parameters:
        sep = ",\\\\" if ixd != 1 else ""
        string+= "\\hspace*{{ 5pt}} {}{}".format(clean_string(p),sep)
        ixd -= 1
    string += ")"

    # end cell 1
    string += " & "

    # cell 2
    string+= ""
    string+=clean_string(m.description)
    
    # end cell 2
    string+="\\\\ \hline \n"
    return string


if __name__ == "__main__":
    classes = parse_jdoc(sys.argv[1])
    exclude_interfaces = True

    classes.sort(key=lambda x: x.module, reverse=True)

    for a in sys.argv[1:]:
        for i, c in enumerate(classes):
            if c.name == a or (exclude_interfaces and c.c_type == "interface"):
                del classes[i]
    
    #TODO: AttributeMap doesnt return the correct name 
    output = open("doc.tex",'w')
    module = None
    prev_module = -1
    for c in classes:
        prev_module = module
        module = c.module

        if module != prev_module:
            output.write("\\subsubsection{{ {} }}\n".format(clean_string(c.module)))

        output.write(format_class_tex(c))
        output.write("\n")


