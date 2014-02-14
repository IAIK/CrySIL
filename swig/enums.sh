createEnum(){
  name=$1
  prefix=$2
  echo "enum ${name} {" > ${prefix}_enum.h
  cat pkcs11t_processed.h | \
  sed -n -e 's/#define[[:blank:]]*'${prefix}'_\([[:alnum:]_]*\)[[:blank:]]*\(0x[[:xdigit:]]*\)[[:blank:]]*/\1=\2,/p;
            s/#define[[:blank:]]*'${prefix}'_\([[:alnum:]_]*\)[[:blank:]]*\([[:digit:]]*\)/\1=\2,/p' >> ${prefix}_enum.h
  echo "LAST };" >> ${prefix}_enum.h
  sed -i 's/^[[:blank:]]*#define[[:blank:]]*'${prefix}'_.*$//p' pkcs11t_processed.h
  sed -i 's/^[[:blank:]]*typedef[[:blank:]]*.*[[:blank:]]*'${name}'[[:blank:]]*.*$//p' pkcs11t_processed.h
}

name="SESSION_STATE"
prefix="CKS"
createEnum $name $prefix

name="ATTRIBUTE_TYPE"
prefix="CKA"
echo "enum ${name} {" > ${prefix}_enum.h
cat pkcs11t_processed.h | \
sed -n -e 's/#define[[:blank:]]*'${prefix}'_\([[:alnum:]_]*\)[[:blank:]]*\(0x[[:xdigit:]]*\|.*\)[[:blank:]]*/\1=\2,/p;
          s/#define[[:blank:]]*'${prefix}'_\([[:alnum:]_]*\)[[:blank:]]*\([[:digit:]]*\)/\1=\2,/p' >> ${prefix}_enum.h
echo "LAST };" >> ${prefix}_enum.h
sed -i 's/^[[:blank:]]*#define[[:blank:]]*'${prefix}'_.*$//p' pkcs11t_processed.h
sed -i 's/^[[:blank:]]*typedef[[:blank:]]*.*[[:blank:]]*'${name}'[[:blank:]]*.*$//p' pkcs11t_processed.h

name="CERT_TYPE"
prefix="CKC"
createEnum $name $prefix

name="KEY_TYP"
prefix="CKK"
echo "enum ${name} {" > ${prefix}_enum.h
cat pkcs11t_processed.h | \
sed -n -e 's/#define[[:blank:]]*'${prefix}'_\([[:alnum:]_]*\)[[:blank:]]*\(0x[[:xdigit:]]*\)[[:blank:]]*/\1_KEY=\2,/p;
          s/#define[[:blank:]]*'${prefix}'_\([[:alnum:]_]*\)[[:blank:]]*\([[:digit:]]*\)/\1=\2,/p' >> ${prefix}_enum.h
echo "LAST };" >> ${prefix}_enum.h
sed -i 's/^[[:blank:]]*#define[[:blank:]]*'${prefix}'_.*$//p' pkcs11t_processed.h
sed -i 's/^[[:blank:]]*typedef[[:blank:]]*.*[[:blank:]]*'${name}'[[:blank:]]*.*$//p' pkcs11t_processed.h

name="MECHANISM_TYPES"
prefix="CKM"
createEnum $name $prefix

name="RETURN_TYPE"
prefix="CKR"
createEnum $name $prefix

name="OBJECT_CLASS"
prefix="CKO"
createEnum $name $prefix


