

createEnum(){
  name=$1
  prefix=$2
  echo "enum ${name} {" > ${prefix}_enum.h
  cat pkcs11t_processed.h | \
  sed -n -e 's/#define[[:blank:]]*'${prefix}'_\([[:alnum:]_]*\)[[:blank:]]*\(0x[[:xdigit:]]*\)[[:blank:]]*/'${prefix}'\1=\2,/p;
            s/#define[[:blank:]]*'${prefix}'_\([[:alnum:]_]*\)[[:blank:]]*\([[:digit:]]*\)/'${prefix}'\1=\2,/p' >> ${prefix}_enum.h
  echo "LAST };" >> ${prefix}_enum.h
}

name="SESSION_STATE"
prefix="CKS"
createEnum $name $prefix

name="ATTRIBUTE_TYPE"
prefix="CKA"
createEnum $name $prefix

name="CERTIFICATE_TYPE"
prefix="CKC"
createEnum $name $prefix

name="KEY_DERIVATION_TYPE"
prefix="CKD"
createEnum $name $prefix

name="HARDWARE_FEATURE_TYPE"
prefix="CKH"
createEnum $name $prefix

name="KEY_TYPE"
prefix="CKK"
createEnum $name $prefix

name="MECHANISM_TYPE"
prefix="CKM"
createEnum $name $prefix

name="RETURN_TYPE"
prefix="CKR"
createEnum $name $prefix



