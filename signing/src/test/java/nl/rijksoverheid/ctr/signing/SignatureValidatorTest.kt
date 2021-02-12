/*
 * Copyright (c) 2020 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *  Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *  SPDX-License-Identifier: EUPL-1.2
 */
package nl.rijksoverheid.ctr.signing

import org.bouncycastle.asn1.x509.SubjectKeyIdentifier
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Base64
import org.junit.Test
import java.io.ByteArrayInputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

private val TEST_SIGNATURE = Base64.decode(
    "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",
)

private val TEST_PAYLOAD = "TEST CONTENT".toByteArray()

class SignatureValidatorTest {

    @Test
    fun `signature validator validates signature with correct signing certificate`() {
        val provider = BouncyCastleProvider()
        val rootCertificate = CertificateFactory.getInstance("X509", provider)
            .generateCertificate(SignatureValidator::class.java.getResourceAsStream("/signingcert.crt")) as X509Certificate
        val trustManager =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        // self-signed
        keyStore.load(null)
        keyStore.setCertificateEntry("ca", rootCertificate)
        trustManager.init(keyStore)

        val trustAnchor =
            SubjectKeyIdentifier.getInstance(rootCertificate.getExtensionValue(org.bouncycastle.asn1.x509.Extension.subjectKeyIdentifier.id))

        val validator = SignatureValidator.Builder()
            .trustAnchorSubjectKeyIdentifier(trustAnchor)
            .trustManager(trustManager.trustManagers[0] as X509TrustManager)
            // self-signed
            .signingCertificate(rootCertificate)
            .build()

        validator.verifySignature(
            ByteArrayInputStream(TEST_PAYLOAD),
            TEST_SIGNATURE
        )
    }

    @Test(expected = SignatureValidationException::class)
    fun `signature validator rejects signature with wrong signing certificate`() {
        val provider = BouncyCastleProvider()
        val rootCertificate = CertificateFactory.getInstance("X509", provider)
            .generateCertificate(SignatureValidator::class.java.getResourceAsStream("/signingcert.crt")) as X509Certificate
        // wrong certificate for signing
        val signingCertificate = CertificateFactory.getInstance("X509", provider)
            .generateCertificate(SignatureValidator::class.java.getResourceAsStream("/ca-pki-overheid.pem")) as X509Certificate
        val trustManager =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        // self-signed
        keyStore.load(null)
        keyStore.setCertificateEntry("ca", rootCertificate)
        trustManager.init(keyStore)

        val trustAnchor =
            SubjectKeyIdentifier.getInstance(rootCertificate.getExtensionValue(org.bouncycastle.asn1.x509.Extension.subjectKeyIdentifier.id))

        val validator = SignatureValidator.Builder()
            .trustAnchorSubjectKeyIdentifier(trustAnchor)
            .trustManager(trustManager.trustManagers[0] as X509TrustManager)
            .signingCertificate(signingCertificate)
            .build()

        validator.verifySignature(
            ByteArrayInputStream(TEST_PAYLOAD),
            TEST_SIGNATURE
        )
    }

    @Test
    fun `signature validator validates correct PKI-O signature`() {
        val provider = BouncyCastleProvider()
        val rootCertificate = CertificateFactory.getInstance("X509", provider)
            .generateCertificate(SignatureValidator::class.java.getResourceAsStream("/ca-pki-overheid.pem")) as X509Certificate

        val trustManager =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        // self-signed
        keyStore.load(null)
        keyStore.setCertificateEntry("ca", rootCertificate)
        trustManager.init(keyStore)

        val trustAnchor =
            SubjectKeyIdentifier.getInstance(rootCertificate.getExtensionValue(org.bouncycastle.asn1.x509.Extension.subjectKeyIdentifier.id))

        // defaults for Staat der Nederlanden trust anchor and authority key id
        val validator = SignatureValidator.Builder()
            .trustAnchorSubjectKeyIdentifier(trustAnchor)
            .trustManager(trustManager.trustManagers[0] as X509TrustManager)
            .build()

        validator.verifySignature(
            ByteArrayInputStream(Base64.decode("eyJyZXN1bHQiOnsidW5pcXVlIjoiN2I3MGI2NTE4OGU4ZmQ3Mjg3YTc0OTY3Mjk0MGQyNGY2N2I5ZmVlMiIsInNhbXBsZURhdGUiOiIyMDIxLTAxLTE4VDIyOjI4OjMzKzAwOjAwIiwidGVzdFR5cGUiOiI3NzVjYWEyMTQ5IiwibmVnYXRpdmVSZXN1bHQiOmZhbHNlfSwicHJvdG9jb2xWZXJzaW9uIjoiMS4wIiwicHJvdmlkZXJJZGVudGlmaWVyIjoiQlJCIiwic3RhdHVzIjoiY29tcGxldGUifQ==")),
            Base64.decode(
                "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"
            )
        )
    }
}
