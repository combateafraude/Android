package com.combateafraude.androidexample;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.combateafraude.activefaceliveness.ActiveFaceLiveness;
import com.combateafraude.activefaceliveness.ActiveFaceLivenessActivity;
import com.combateafraude.activefaceliveness.ActiveFaceLivenessResult;
import com.combateafraude.documentdetector.DocumentDetector;
import com.combateafraude.documentdetector.DocumentDetectorActivity;
import com.combateafraude.documentdetector.DocumentDetectorResult;
import com.combateafraude.documentdetector.configuration.Document;
import com.combateafraude.documentdetector.configuration.DocumentDetectorStep;
import com.combateafraude.faceauthenticator.FaceAuthenticator;
import com.combateafraude.faceauthenticator.FaceAuthenticatorActivity;
import com.combateafraude.faceauthenticator.FaceAuthenticatorResult;
import com.combateafraude.helpers.sdk.failure.InvalidTokenReason;
import com.combateafraude.helpers.sdk.failure.LibraryReason;
import com.combateafraude.helpers.sdk.failure.NetworkReason;
import com.combateafraude.helpers.sdk.failure.PermissionReason;
import com.combateafraude.helpers.sdk.failure.SDKFailure;
import com.combateafraude.helpers.sdk.failure.ServerReason;
import com.combateafraude.helpers.sdk.failure.StorageReason;
import com.combateafraude.passivefaceliveness.PassiveFaceLiveness;
import com.combateafraude.passivefaceliveness.PassiveFaceLivenessActivity;
import com.combateafraude.passivefaceliveness.PassiveFaceLivenessResult;

public class MainActivity extends AppCompatActivity {

    // The CAF mobile_token, needed to start the SDKs. To request one, mail to frederico.gassen@combateafraude.com.
    private static final String MOBILE_TOKEN = "INSERT_YOUR_MOBILE_TOKEN";

    // The registered CPF used in face authenticator
    private static final String CPF = "INSERT_YOUR_CPF";

    // The default flow to scan a front and a back of CNH
    private static final DocumentDetectorStep[] CNH_FLOW = DocumentDetector.CNH_FLOW;

    // The default flow to scan a front and a back of RG
    private static final DocumentDetectorStep[] RG_FLOW = DocumentDetector.RG_FLOW;

    // A example of generic flow that scan only one generic document (like OAB, Identidade Militar or RNE),
    // except RGs and CNHs. You can create whatever DocumentDetectorFlow you want, just need
    // to pass which Document want to be detected in the first parameter
    private static final DocumentDetectorStep[] ONE_GENERIC_DOCUMENT_FLOW = new DocumentDetectorStep[]{
            new DocumentDetectorStep(Document.GENERIC, null, null, null, null)
    };

    // REQUEST_CODES to identify what activity is been started and know which result get in onActivityResult method
    private static final int DOCUMENT_DETECTOR_CODE = 1;
    private static final int PASSIVE_FACE_LIVENESS_CODE = 2;
    private static final int ACTIVE_FACE_LIVENESS_CODE = 3;
    private static final int FACE_AUTHENTICATOR = 4;

    // REQUEST_CODE to request permissions
    private static final int PERMISSIONS_CODE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request all required permissions to run this example. To check the individual SDK permissions and requested only the individual SDK permissions, please check https://github.com/combateafraude/Android/wiki
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_CODE);
    }


    /**
     * The DocumentDetector SDK is usually called in an onboarding flow of some app that requires the user documents. Why not call it instead of calling a native camera to ensure the quality and a real document photo?
     */
    public void documentDetector(View view) {
        // Create the DocumentDetector parameter
        DocumentDetector documentDetector = new DocumentDetector.Builder(MOBILE_TOKEN)
                .setDocumentDetectorFlow(CNH_FLOW) // Set what flow the SDK will run
                // other optional parameters like style, layout, request timeout, etc. For more information, go to https://github.com/combateafraude/Android/wiki
                .build();

        // Start the DocumentDetectorActivity passing the DocumentDetector object by parameter. The result will be collected in onActivityResult method below
        Intent intent = new Intent(this, DocumentDetectorActivity.class);
        intent.putExtra(DocumentDetector.PARAMETER_NAME, documentDetector);
        startActivityForResult(intent, DOCUMENT_DETECTOR_CODE);
    }

    /**
     * The PassiveFaceLiveness SDK can replace any selfie capture, with anti spoofing advantage and real selfie capture
     */
    public void passiveFaceLiveness(View view) {
        // Create the PassiveFaceLiveness parameter
        PassiveFaceLiveness passiveFaceLiveness = new PassiveFaceLiveness.Builder(MOBILE_TOKEN)
                // other optional parameters like style, layout, request timeout, etc. For more information, go to https://github.com/combateafraude/Android/wiki
                .build();

        // Start the PassiveFaceLivenessActivity passing the DocumentDetector object by parameter. The result will be collected in onActivityResult method below
        Intent intent = new Intent(this, PassiveFaceLivenessActivity.class);
        intent.putExtra(PassiveFaceLiveness.PARAMETER_NAME, passiveFaceLiveness);
        startActivityForResult(intent, PASSIVE_FACE_LIVENESS_CODE);
    }

    /**
     * The ActiveFaceLiveness SDK can replace any selfie capture. The facial movements required will prevent third photos
     */
    public void activeFaceLiveness(View view) {
        // Create the ActiveFaceLiveness parameter
        ActiveFaceLiveness activeFaceLiveness = new ActiveFaceLiveness.Builder(MOBILE_TOKEN)
                // other optional parameters like action number, action timeout, style, layout, request timeout, etc. For more information, go to https://github.com/combateafraude/Android/wiki
                .build();

        // Start the ActiveFaceLivenessActivity passing the DocumentDetector object by parameter. The result will be collected in onActivityResult method below
        Intent intent = new Intent(this, ActiveFaceLivenessActivity.class);
        intent.putExtra(ActiveFaceLiveness.PARAMETER_NAME, activeFaceLiveness);
        startActivityForResult(intent, ACTIVE_FACE_LIVENESS_CODE);
    }

    /**
     * The FaceAuthenticator SDK can be used to increase the security of your app, in addition to verify spoofing photos. Why not call it in a login flow or a money operation?
     */
    public void faceAuthenticator(View view) {
        // Create the FaceAuthenticator parameter
        FaceAuthenticator faceAuthenticator = new FaceAuthenticator.Builder(MOBILE_TOKEN)
                .setCpf(CPF) // the CPF that has the registered face in CAF server. To register one, you need to create an execution here: https://docs.combateafraude.com/docs/integracao-api/enviar-documento-analise/
                // other optional parameters like style, layout, request timeout, etc. For more information, go to https://github.com/combateafraude/Android/wiki
                .build();

        // Start the FaceAuthenticatorActivity passing the DocumentDetector object by parameter. The result will be collected in onActivityResult method below
        Intent intent = new Intent(this, FaceAuthenticatorActivity.class);
        intent.putExtra(FaceAuthenticator.PARAMETER_NAME, faceAuthenticator);
        startActivityForResult(intent, FACE_AUTHENTICATOR);
    }


    // Note: this method is a little complex only because we are calling all SDKs in the same activity. In a real situation, this won't happen.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        // Discover if the SDK had finished with some result
        if (resultCode == RESULT_OK && data != null) {
            // Discover what SDK had finished
            switch (requestCode) {
                case DOCUMENT_DETECTOR_CODE:
                    DocumentDetectorResult documentDetectorResult = (DocumentDetectorResult) data.getSerializableExtra(DocumentDetectorResult.PARAMETER_NAME);
                    if (documentDetectorResult != null) {
                        postDocumentDetector(documentDetectorResult);
                    } else {
                        Toast.makeText(this, "Should never get here!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case PASSIVE_FACE_LIVENESS_CODE:
                    PassiveFaceLivenessResult passiveFaceLivenessResult = (PassiveFaceLivenessResult) data.getSerializableExtra(PassiveFaceLivenessResult.PARAMETER_NAME);
                    if (passiveFaceLivenessResult != null) {
                        postPassiveFaceLiveness(passiveFaceLivenessResult);
                    } else {
                        Toast.makeText(this, "Should never get here!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case ACTIVE_FACE_LIVENESS_CODE:
                    ActiveFaceLivenessResult activeFaceLivenessResult = (ActiveFaceLivenessResult) data.getSerializableExtra(ActiveFaceLivenessResult.PARAMETER_NAME);
                    if (activeFaceLivenessResult != null) {
                        postActiveFaceLiveness(activeFaceLivenessResult);
                    } else {
                        Toast.makeText(this, "Should never get here!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case FACE_AUTHENTICATOR:
                    FaceAuthenticatorResult faceAuthenticatorResult = (FaceAuthenticatorResult) data.getSerializableExtra(FaceAuthenticatorResult.PARAMETER_NAME);
                    if (faceAuthenticatorResult != null) {
                        postFaceAuthenticator(faceAuthenticatorResult);
                    } else {
                        Toast.makeText(this, "Should never get here!", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        } else {
            Toast.makeText(this, "You closed the SDK activity!", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void postDocumentDetector(DocumentDetectorResult documentDetectorResult) {
        SDKFailure sdkFailure = documentDetectorResult.getSdkFailure();
        if (sdkFailure == null) {
            Toast.makeText(this, "SDK successfully finished", Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof InvalidTokenReason) {
            Toast.makeText(this, "Invalid token", Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof PermissionReason) {
            Toast.makeText(this, "One or more permission is missing:" + sdkFailure.getMessage(), Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof NetworkReason) {
            Toast.makeText(this, "You don't have internet or the request exceeded the timeout", Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof ServerReason) {
            Toast.makeText(this, "There is some server error. Please, notify us: " + sdkFailure.getMessage(), Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof StorageReason) {
            Toast.makeText(this, "The SDK couldn't save the image because the device doesn't have enough space", Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof LibraryReason) {
            Toast.makeText(this, "One internal library failed to execute: " + sdkFailure.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void postPassiveFaceLiveness(PassiveFaceLivenessResult passiveFaceLivenessResult) {
        SDKFailure sdkFailure = passiveFaceLivenessResult.getSdkFailure();
        if (sdkFailure == null) {
            Toast.makeText(this, "SDK successfully finished", Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof InvalidTokenReason) {
            Toast.makeText(this, "Invalid token", Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof PermissionReason) {
            Toast.makeText(this, "One or more permission is missing:" + sdkFailure.getMessage(), Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof NetworkReason) {
            Toast.makeText(this, "You don't have internet or the request exceeded the timeout", Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof ServerReason) {
            Toast.makeText(this, "There is some server error. Please, notify us: " + sdkFailure.getMessage(), Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof StorageReason) {
            Toast.makeText(this, "The SDK couldn't save the image because the device doesn't have enough space", Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof LibraryReason) {
            Toast.makeText(this, "One internal library failed to execute: " + sdkFailure.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void postActiveFaceLiveness(ActiveFaceLivenessResult activeFaceLivenessResult) {
        SDKFailure sdkFailure = activeFaceLivenessResult.getSdkFailure();
        if (sdkFailure == null) {
            Toast.makeText(this, "SDK successfully finished", Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof InvalidTokenReason) {
            Toast.makeText(this, "Invalid token", Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof PermissionReason) {
            Toast.makeText(this, "One or more permission is missing:" + sdkFailure.getMessage(), Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof NetworkReason) {
            Toast.makeText(this, "You don't have internet or the request exceeded the timeout", Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof ServerReason) {
            Toast.makeText(this, "There is some server error. Please, notify us: " + sdkFailure.getMessage(), Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof StorageReason) {
            Toast.makeText(this, "The SDK couldn't save the image because the device doesn't have enough space", Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof LibraryReason) {
            Toast.makeText(this, "One internal library failed to execute: " + sdkFailure.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void postFaceAuthenticator(FaceAuthenticatorResult faceAuthenticatorResult) {
        SDKFailure sdkFailure = faceAuthenticatorResult.getSdkFailure();
        if (sdkFailure == null) {
            Toast.makeText(this, "SDK successfully finished. Authenticated? " + faceAuthenticatorResult.isAuthenticated(), Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof InvalidTokenReason) {
            Toast.makeText(this, "Invalid token", Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof PermissionReason) {
            Toast.makeText(this, "One or more permission is missing:" + sdkFailure.getMessage(), Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof NetworkReason) {
            Toast.makeText(this, "You don't have internet or the request exceeded the timeout", Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof ServerReason) {
            Toast.makeText(this, "There is some server error. Please, notify us: " + sdkFailure.getMessage(), Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof StorageReason) {
            Toast.makeText(this, "The SDK couldn't save the image because the device doesn't have enough space", Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof LibraryReason) {
            Toast.makeText(this, "One internal library failed to execute: " + sdkFailure.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}