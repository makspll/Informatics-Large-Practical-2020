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

def format_class_tex(c,all_classes):
    string = ""

    string += "\cbox{{ {} {} {} }} {{\\scriptsize {}\n \n".format("abstract" if c.is_abstract else "",clean_string(c.c_type),clean_string(c.name),format_class_names(clean_string(c.description),all_classes))
    
    if c.implements != [] or c.extends is not None:
        string += "\\vspace*{4pt} \\hrule \\vspace*{3pt}\n"

    if c.implements != []:
        for i in c.implements:
            string += "Implements \\textbf{{ {} }}\n".format(format_class_names(clean_string(i),all_classes))
    if c.extends is not None:
            string += "Extends \\textbf{{ {} }}\n".format(format_class_names(clean_string(c.extends),all_classes))

    if len(c.methods) > 0:
        string+= "\\vspace*{-5pt}\\tcbsubtitle[before skip=\\baselineskip]{Members} \n"

    if c.methods != []:
        string+= "\\begin{tabularx}{\\linewidth}{m{0.45\\textwidth}|m{0.5\\textwidth}}\n"
        string+= "\\label{{tab:{}}}\n".format(c.name)
        i = 0
        for m in c.methods:
            string += format_method(m,i,len(c.methods),all_classes)
            i+= 1
        string += "\\end{tabularx}\n"
    else:
        string+= "\\label{{tab:{}}}\n".format(c.name)

    string += "}"
    
    return string

def format_class_names(class_latex_string,classes):
    """
    adds hyperlinks and shortens names
    """
    hyperlinked_string = class_latex_string
    hyperlinked_string = hyperlinked_string.split(" ")
    new_strings = []
    classes_sorted_by_name_length = sorted(classes,key =lambda x : -len(x.name))
    for w in hyperlinked_string:
        new_string = w
        for c in classes_sorted_by_name_length:
            if c.name.strip() == w:
                display_name = c.name.split(".")[-1]
                new_string = (w.replace(c.name, "\\hyperref[tab:{}]{{\\color{{blue}}{{{}}}}}".format(c.name,display_name)))
                break
        new_strings.append(new_string)
        

    return " ".join(new_strings)
    


def format_method(m,idx,methodCount,all_classes):
    string = ""

    # raggedleft
    string += "\\begin{raggedleft}"
    # cell 1

    signaturesplit = m.signature.split(" ")
    visibility = signaturesplit[0]
    
    middle = " ".join(signaturesplit[1:-1])
    funcName = signaturesplit[-1]

    string+= "{} {} \\textbf{{{}}}(".format(clean_string(visibility),format_class_names(clean_string(middle),all_classes), format_class_names(clean_string(funcName),all_classes))
    ixd = len(m.parameters)

    if len(m.parameters) > 1:
        string += "\\newline \\hfill \n"
        pass

    for p in m.parameters:
        sep = ", \\newline\n" if ixd != 1 else ""
        string+= "\\hspace*{{ 5pt}} \\textbf{{{}}} {} {} ".format(format_class_names(clean_string(p.class_type),all_classes),clean_string(p.name),sep)
        ixd -= 1
    string += ")\n"

    # end raggedleft
    string += "\\end{raggedleft}"

    # end cell 1
    string += " &\n "

    # cell 2
    string+= ""
    string+=format_class_names(clean_string(m.description),all_classes)
    
    # end cell 2
    string+="\\\\ \hline \n" if idx != methodCount -1 else "\\\\"

    return string


if __name__ == "__main__":

    exclusions = {
        "W3WSquareData":["public com.mapbox.geojson.Point getSouthwest","public com.mapbox.geojson.Point getNorthwest"],
        "W3WAddressData":["public String getCountry",
                            "public W3WSquareData getSquare",
                            "public String getNearestPlace",
                            "public com.mapbox.geojson.Point getCoordinates",
                            "public String getWords",
                            "public String getLanguage",
                            "public String getMap"],
        "SensorData":["public String getW3WLocation",
                      "public Float getBattery",
                      "public float getReading"],
        "SearchNode":["public double getHeuristic",
                        "public org.locationtech.jts.geom.Coordinate getCoordinates",
                        "public void setHeuristic",
                        "public double getCost",
                        "public T getParentNode",
                        "public Deque<PathfindingGoal> getGoalsReached",
                        "public void addGoalReached​",
                        "public void setGoalsReached​",
                        "public void removeGoalReached​",
                        "public int getNumberOfGoalsReached",
                        "public void setHeuristic​"],
        "PointGoal":["public org.locationtech.jts.geom.Coordinate getCoordinates"],
        "SensorDataCollectorFactory":[2],
        "Sensor":["public String getW3WLocation",
                   "public org.locationtech.jts.geom.Coordinate getCoordinates" ],
        "DirectedSearchNode":["public int getDirectionFromParent",
                                0],
        "Building":["public org.locationtech.jts.geom.Polygon getShape"],
        "ConstrainedTreeGraph":[1,
                                "public Collection<Obstacle> getObstacles",
                                "public org.locationtech.jts.geom.Polygon getBoundary",
                                "public double getMoveLength"],
        "PathSegment":[1,2,3,4]
    }
    classes = parse_jdoc(sys.argv[1])
    exclude_interfaces = True

    classes.sort(key=lambda x: x.module, reverse=True)

    for a in sys.argv[1:] + ["App","HTTPException"]:
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
        if c.extends == "Object":
            c.extends = None

        leftover_ms = []
        excluded_methods = exclusions[c.name] if c.name in exclusions else []
        idx = 0
        for m in c.methods:
            if not("toString" in m.signature  or "equals" in m.signature or "hashCode" in m.signature) and not (m.signature in excluded_methods) and not(idx in excluded_methods):
                leftover_ms.append(m)
                print ('.'+ str(m.signature) +'.')
            idx += 1
        c.methods = leftover_ms

        if module != prev_module:
            output.write("\\subsubsection*{{ {} }}\n".format(clean_string(c.module)))

        output.write(format_class_tex(c,classes))
        output.write("\n")

    # this needs to be copied in manually to the main tex file
    preamble = open("paste-me-directly.tex","w")
    preamble.write("\\usepackage[hidelinks]{hyperref}\n")
    preamble.write("\\usepackage{tcolorbox}\n")
    preamble.write("\\usepackage{array}\n")
    preamble.write("\\usepackage{tabularx}\n")
    preamble.write("\\AtBeginEnvironment{tabularx}{\\scriptsize}\n")
    preamble.write("\\renewcommand{\\tabularxcolumn}[1]{m{#1}}\n")
    preamble.write("\\setlist{nolistsep}\n")
    preamble.write("\\setlist{nosep}\n")
    preamble.write("\\newcommand{\\cbox}[2]{\n")
    preamble.write("    \\begin{tcolorbox}[title=#1,\n")
    preamble.write("        colback=red!5!white,\n")
    preamble.write("        colframe=red!50!black,\n")
    preamble.write("        size=fbox,\n")
    preamble.write("        % left=2mm,\n")
    preamble.write("        % right=3mm,\n")
    preamble.write("        fonttitle=\\bfseries]\n")
    preamble.write("        #2\n")
    preamble.write("    \\end{tcolorbox}\n")
    preamble.write("}\n")
