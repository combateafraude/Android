package com.miguelxcruz.example;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.combateafraude.documentdetector.DocumentDetectorActivity;
import com.combateafraude.documentdetector.input.Document;
import com.combateafraude.documentdetector.input.DocumentDetector;
import com.combateafraude.documentdetector.input.DocumentDetectorStep;
import com.combateafraude.documentdetector.output.DocumentDetectorResult;
import com.combateafraude.documentdetector.output.failure.InvalidTokenReason;
import com.combateafraude.documentdetector.output.failure.LibraryReason;
import com.combateafraude.documentdetector.output.failure.NetworkReason;
import com.combateafraude.documentdetector.output.failure.PermissionReason;
import com.combateafraude.documentdetector.output.failure.SDKFailure;
import com.combateafraude.documentdetector.output.failure.ServerReason;
import com.combateafraude.documentdetector.output.failure.StorageReason;
import com.combateafraude.faceauthenticator.FaceAuthenticatorActivity;
import com.combateafraude.faceauthenticator.input.FaceAuthenticator;
import com.combateafraude.faceauthenticator.output.FaceAuthenticatorResult;
import com.combateafraude.passivefaceliveness.PassiveFaceLivenessActivity;
import com.combateafraude.passivefaceliveness.input.PassiveFaceLiveness;
import com.combateafraude.passivefaceliveness.output.PassiveFaceLivenessResult;

import java.lang.annotation.Documented;

//This app is a demonstration of the SDK's implemantation that you can use while looking at our documentation
//To see the combate a fraude SDK documentation use the following link: https://docs.combateafraude.com/docs/mobile/introduction/home/

public class MainActivity extends AppCompatActivity {

    // The CAF mobile_token, needed to start the SDKs. To request one, mail to daniel.seitenfus@combateafraude.com
    private static final String MOBILE_TOKEN = "INSERT_YOUR_MOBILE_TOKEN";

    // The registered CPF used in face authenticator
    private static final String CPF = "INSERT_YOUR_CPF";

    // The default flow to scan a front and a back of CNH
    private static final DocumentDetectorStep[] CNH_FLOW = new DocumentDetectorStep[]{
            new DocumentDetectorStep(Document.CNH_FRONT),
            new DocumentDetectorStep(Document.CNH_BACK)
            //You can also set another configuration that can be acessed on: https://docs.combateafraude.com/docs/mobile/android/document-detector/
    };

    // The default flow to scan a front and a back of RG
    private static final DocumentDetectorStep[] RG_FLOW = new DocumentDetectorStep[]{
            new DocumentDetectorStep(Document.RG_FRONT),
            new DocumentDetectorStep(Document.RG_BACK)
            //You can also set another configuration that can be acessed on: https://docs.combateafraude.com/docs/mobile/android/document-detector/
    };

    // A example of generic flow that scan only one generic document (like OAB, Identidade Militar or RNE) -> To user a GENERIC Document user OTHERS,
    // except RGs and CNHs. You can create whatever DocumentDetectorFlow you want, just need
    // to pass which Document want to be detected in the parameter
    private static final DocumentDetectorStep[] ONE_GENERIC_DOCUMENT_FLOW = new DocumentDetectorStep[]{
            new DocumentDetectorStep(Document.OTHERS)
            //You can also set another configuration that can be acessed on: https://docs.combateafraude.com/docs/mobile/android/document-detector/
    };


    // REQUEST_CODES to identify what activity is been started and know which result get in onActivityResult method
    private static final int DOCUMENT_DETECTOR_CODE = 1;
    private static final int PASSIVE_FACE_LIVENESS_CODE = 2;
    private static final int FACE_AUTHENTICATOR = 4;

    // REQUEST_CODE to request permissions
    private static final int PERMISSIONS_CODE = 5;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request all required permissions to run this example. To check the individual SDK permissions and requested only the individual SDK permissions, please check https://docs.combateafraude.com/docs/mobile/introduction/home/
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_CODE);
    }

    /**
     * The DocumentDetector SDK is usually called in an onboarding flow of some app that requires the user documents. Why not call it instead of calling a native camera to ensure the quality and a real document photo?
     */
    public void documentDetector(View view) {
        // Create the DocumentDetector parameter
        DocumentDetector documentDetector = new DocumentDetector.Builder(MOBILE_TOKEN)
                .setDocumentSteps(CNH_FLOW)
                .setUseEmulator(true)
                .build();
        //set the flow of the document capture. documentSteps is expecting to receive a document declaration. exemple: RG_DECLARATION
        // other optional parameters like style, layout, request timeout, etc. For more information, go to https://docs.combateafraude.com/docs/mobile/android/document-detector/


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
                .setUseEmulator(true)
                // other optional parameters like style, layout, request timeout, etc. For more information, go to https://docs.combateafraude.com/docs/mobile/android/passive-face-liveness/
                .build();

        // Start the PassiveFaceLivenessActivity passing the DocumentDetector object by parameter. The result will be collected in onActivityResult method below
        Intent intent = new Intent(this, PassiveFaceLivenessActivity.class);
        intent.putExtra(PassiveFaceLiveness.PARAMETER_NAME, passiveFaceLiveness);
        startActivityForResult(intent, PASSIVE_FACE_LIVENESS_CODE);
    }

    /**
     * The FaceAuthenticator SDK can be used to increase the security of your app, in addition to verify spoofing photos. Why not call it in a login flow or a money operation?
     */
    public void faceAuthenticator(View view) {
        // Create the FaceAuthenticator parameter
        FaceAuthenticator faceAuthenticator = new FaceAuthenticator.Builder(MOBILE_TOKEN)
                .setPeopleId(CPF) // the CPF that has the registered face in CAF server. To register one, you need to create an execution here: https://docs.combateafraude.com/docs/integracao-api/enviar-documento-analise/
                // other optional parameters like style, layout, request timeout, etc. For more information, go to https://docs.combateafraude.com/docs/mobile/android/face-authenticator/
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
            // Discover what SDK had finished (In case of Using diferents class for which one with won't be necessary)
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

    //You also have the reasons for failures that can be acessed on our documentation: https://docs.combateafraude.com/docs/mobile/android/sdk-failure/
    //Failures that can happen using DocumentDetector's SDK
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

    //Failures that can happen using PassiveFaceLiveness's SDK
    private void postPassiveFaceLiveness(PassiveFaceLivenessResult passiveFaceLivenessResult) {
        com.combateafraude.passivefaceliveness.output.failure.SDKFailure sdkFailure;
        sdkFailure = passiveFaceLivenessResult.getSdkFailure();
        if (sdkFailure == null) {
            Toast.makeText(this, "SDK successfully finished", Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof com.combateafraude.passivefaceliveness.output.failure.InvalidTokenReason) {
            Toast.makeText(this, "Invalid token", Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof com.combateafraude.passivefaceliveness.output.failure.PermissionReason) {
            Toast.makeText(this, "One or more permission is missing:" + sdkFailure.getMessage(), Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof com.combateafraude.passivefaceliveness.output.failure.NetworkReason) {
            Toast.makeText(this, "You don't have internet or the request exceeded the timeout", Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof com.combateafraude.passivefaceliveness.output.failure.ServerReason) {
            Toast.makeText(this, "There is some server error. Please, notify us: " + sdkFailure.getMessage(), Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof com.combateafraude.passivefaceliveness.output.failure.StorageReason) {
            Toast.makeText(this, "The SDK couldn't save the image because the device doesn't have enough space", Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof com.combateafraude.passivefaceliveness.output.failure.LibraryReason) {
            Toast.makeText(this, "One internal library failed to execute: " + sdkFailure.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //Failures that can happen using FaceAuthenticator's SDK
    private void postFaceAuthenticator(FaceAuthenticatorResult faceAuthenticatorResult) {
        com.combateafraude.faceauthenticator.output.failure.SDKFailure sdkFailure = faceAuthenticatorResult.getSdkFailure();
        if (sdkFailure == null) {
            Toast.makeText(this, "SDK successfully finished. Authenticated? " + faceAuthenticatorResult.isAuthenticated(), Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof com.combateafraude.faceauthenticator.output.failure.InvalidTokenReason) {
            Toast.makeText(this, "Invalid token", Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof com.combateafraude.faceauthenticator.output.failure.PermissionReason) {
            Toast.makeText(this, "One or more permission is missing:" + sdkFailure.getMessage(), Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof com.combateafraude.faceauthenticator.output.failure.NetworkReason) {
            Toast.makeText(this, "You don't have internet or the request exceeded the timeout", Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof com.combateafraude.faceauthenticator.output.failure.ServerReason) {
            Toast.makeText(this, "There is some server error. Please, notify us: " + sdkFailure.getMessage(), Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof com.combateafraude.faceauthenticator.output.failure.StorageReason) {
            Toast.makeText(this, "The SDK couldn't save the image because the device doesn't have enough space", Toast.LENGTH_SHORT).show();
        } else if (sdkFailure instanceof com.combateafraude.faceauthenticator.output.failure.LibraryReason) {
            Toast.makeText(this, "One internal library failed to execute: " + sdkFailure.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}