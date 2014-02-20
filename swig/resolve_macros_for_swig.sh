#!/bin/sh

sed 's/CK_PTR/*/g' | \
sed 's/CK_DEFINE_FUNCTION(\([^,)]*\), \([^,)]*\))/\1 \2/g' | \

# * #define CK_DECLARE_FUNCTION_POINTER(returnType, name) \
# *   returnType (* name)
sed 's/CK_DECLARE_FUNCTION_POINTER(\([^,)]*\), \([^,)]*\))/\1 (* \2)/g' | \


# * #define CK_DECLARE_FUNCTION(returnType, name) \
# *   returnType name
sed 's/CK_DECLARE_FUNCTION(\([^,)]*\), \([^,)]*\))/\1 \2/g' | \

# * #define CK_CALLBACK_FUNCTION(returnType, name) \
# *   returnType (* name)
sed 's/CK_CALLBACK_FUNCTION(\([^,)]*\), \([^,)]*\))/\1 (* \2)/g' | \

sed 's/NULL_PTR/0/g' | \

# * add size parameter to each struct
sed -e '/^} \([[:alnum:]_]*\);/ { 
																N
					s/} \([[:alnum:]_]*\);/} \1;\n %extend \1 { \n size_t getSize(){ \n    return sizeof(\1); \n } \n }/
																			} '  | \
# * add size parameter to each struct
#sed -e '/typedef struct [[:alnum:]_]* {/ a\ %extend{ %immutable size; \n size_t size; }' -e '/^} \([[:alnum:]_]*\);/ { 
#																																											N
#					s/} \([[:alnum:]_]*\);/} \1;\n %{ \n size_t \1_size_get(){ \n    return sizeof(\1); \n } \n void \1_size_set(\1* obj,size_t s){ return; } \n %}   /
#																			} '  | \

sed '/#define CKF_ARRAY_ATTRIBUTE/!s/CKF_ARRAY_ATTRIBUTE/0x40000000/g'

################################Make Funktion Deklerations#####################################################
#
#cat "pkcs11f.h" | sed 's/CK_PTR/*/g' | \
#sed 's/CK_DEFINE_FUNCTION(\([^,)]*\), \([^,)]*\))/\1 \2/g' | \

#sed 's/CK_PKCS11_FUNCTION_INFO(\([^)]*\))/extern CK_DECLARE_FUNCTION(CK_RV, \1)/g' | \
#sed 's/#ifdef CK_NEED_ARG_LIST//g' | \
#sed 's/#endif//g' | \

# * #define CK_DECLARE_FUNCTION_POINTER(returnType, name) \
# *   returnType (* name)
#sed 's/CK_DECLARE_FUNCTION_POINTER(\([^,)]*\), \([^,)]*\))/\1 (* \2)/g' | \
# * #define CK_DECLARE_FUNCTION(returnType, name) \
# *   returnType name
#sed 's/CK_DECLARE_FUNCTION(\([^,)]*\), \([^,)]*\))/\1 \2/g' | \

# * #define CK_CALLBACK_FUNCTION(returnType, name) \
# *   returnType (* name)
#sed 's/CK_CALLBACK_FUNCTION(\([^,)]*\), \([^,)]*\))/\1 (* \2)/g' | \

#sed 's/NULL_PTR/0/g' > pkcs11f_funcdecl.h
#################################Make typedefs for Funktion Pointers#######################################

#cat "pkcs11f.h" | sed 's/CK_PTR/*/g' | \
#sed 's/CK_DEFINE_FUNCTION(\([^,)]*\), \([^,)]*\))/\1 \2/g' | \

#sed 's/CK_PKCS11_FUNCTION_INFO(\([^)]*\))/typedef CK_DECLARE_FUNCTION_POINTER(CK_RV, CK_\1)/g' | \
#sed 's/#ifdef CK_NEED_ARG_LIST//g' | \
#sed 's/#endif//g' | \

# * #define CK_DECLARE_FUNCTION_POINTER(returnType, name) \
# *   returnType (* name)
#sed 's/CK_DECLARE_FUNCTION_POINTER(\([^,)]*\), \([^,)]*\))/\1 (* \2)/g' | \
# * #define CK_DECLARE_FUNCTION(returnType, name) \
# *   returnType name
#sed 's/CK_DECLARE_FUNCTION(\([^,)]*\), \([^,)]*\))/\1 \2/g' | \

# * #define CK_CALLBACK_FUNCTION(returnType, name) \
# *   returnType (* name)
#sed 's/CK_CALLBACK_FUNCTION(\([^,)]*\), \([^,)]*\))/\1 (* \2)/g' | \

#sed 's/NULL_PTR/0/g' > pkcs11f_funcpointer.h
