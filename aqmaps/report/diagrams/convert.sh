

for i in *.uxf; do
    [ -f "$i" ] || break
    echo $i
    umlet -action=convert -format=pdf -filename=$i
done
