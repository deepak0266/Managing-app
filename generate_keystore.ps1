# PowerShell script to generate a standard release keystore
# Run this from the root of your project directory

$KeystoreName = "app/release-keystore.jks"
$Alias = "lifeos_key"
$Password = "lifeos_password123" # In a real scenario, use a secure password
$Validity = 10000

Write-Host "Generating Release Keystore..."

# This requires Java's 'keytool' to be in your PATH (installed with JDK/Android Studio)
keytool -genkey -v -keystore $KeystoreName -alias $Alias -keyalg RSA -keysize 2048 -validity $Validity -storepass $Password -keypass $Password -dname "CN=LifeOS, OU=Development, O=Personal, L=City, S=State, C=US"

Write-Host "Done! Keystore created at $KeystoreName"
Write-Host "Update your local.properties with:"
Write-Host "KEYSTORE_PATH=release-keystore.jks"
Write-Host "KEYSTORE_PASSWORD=$Password"
Write-Host "KEYSTORE_ALIAS=$Alias"
Write-Host "KEY_PASSWORD=$Password"
