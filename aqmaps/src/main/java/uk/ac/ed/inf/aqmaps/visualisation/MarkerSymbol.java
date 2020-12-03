package uk.ac.ed.inf.aqmaps.visualisation;

/**
 * Available symbols for use with geojson, the toString methods are overriden to conform to the geojson marker list
 */
public enum MarkerSymbol {
    LIGHTHOUSE{
        public String toString(){
            return "lighthouse";
        }
    },
    DANGER{
        public String toString(){
            return "danger";
        }
    },
    CROSS{
        public String toString(){
            return "cross";
        }
    },
    NO_SYMBOL{
        public String toString(){
            return "no symbol";
        }
    }
}
