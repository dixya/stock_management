#! /bin/bash

# This script creates two key stores and two corresponding trust stores.
if [ $# -eq 0 ]; then
    echo "Target directory paramater required. (ex. src/main/resources)"
	exit 1
fi

GEN_REZ_DIR=$1
mkdir -p $GEN_REZ_DIR

#Generates an RSA key and places it in a keystore, extracts the public key certificate
function genKey {
     KN=$1
	 TN=$2
	 # Create keystore, this will contain an asymmetric key pair
	 KEY_STORE=$GEN_REZ_DIR/${KN}Key.jck
	 if [ ! -e $KEY_STORE ]; then
		keytool -genkeypair -keyalg RSA -keysize 512 -validity 365 \
				-dname "cn=$KN, ou=cp130, o=UW, l=Seattle, st=Washington, c=US" \
				-alias ${KN}PrivKey -keypass ${KN}PrivKeyPass \
				-storetype JCEKS -keystore $KEY_STORE -storepass ${KN}StorePass
	else  
	    echo "Keystore already exists, skipping creation: $KEY_STORE"
	fi
	
	# Create a trust store containing the public key certificate
	TRUST_STORE=$GEN_REZ_DIR/${TN}Trust.jck
	if [ ! -e $TRUST_STORE ]; then
		#Extract the certificate from the keystore
		TMP_CERT_FILE=/tmp/${KN}_$$.crt
		keytool -export -file $TMP_CERT_FILE \
				-alias ${KN}PrivKey -keypass ${KN}PriveKeyPass \
				-storetype JCEKS -keystore $KEY_STORE -storepass ${KN}StorePass
		  
		#Place the certificate in a trust store
		keytool  -importcert -noprompt -file $TMP_CERT_FILE \
				 -alias ${KN}Cert \
				 -storetype JCEKS -keystore $TRUST_STORE -storepass ${TN}TrustPass
		rm -f $TMP_CERT_FILE
	else
		echo "Truststore already exist, skipping creation: $TRUST_STORE"
	fi
}

genKey client broker
genKey broker client
	