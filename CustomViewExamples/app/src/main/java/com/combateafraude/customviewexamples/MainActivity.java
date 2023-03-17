package com.combateafraude.customviewexamples;

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
import com.combateafraude.documentdetector.input.MessageSettings;
import com.combateafraude.documentdetector.input.PreviewSettings;
import com.combateafraude.documentdetector.output.DocumentDetectorResult;
import com.combateafraude.documentdetector.output.failure.AvailabilityReason;
import com.combateafraude.documentdetector.output.failure.InvalidTokenReason;
import com.combateafraude.documentdetector.output.failure.LibraryReason;
import com.combateafraude.documentdetector.output.failure.NetworkReason;
import com.combateafraude.documentdetector.output.failure.PermissionReason;
import com.combateafraude.documentdetector.output.failure.SDKFailure;
import com.combateafraude.documentdetector.output.failure.SecurityReason;
import com.combateafraude.documentdetector.output.failure.ServerReason;
import com.combateafraude.documentdetector.output.failure.StorageReason;
import com.combateafraude.faceauthenticator.FaceAuthenticatorActivity;
import com.combateafraude.faceauthenticator.input.FaceAuthenticator;
import com.combateafraude.faceauthenticator.output.FaceAuthenticatorResult;
import com.combateafraude.passivefaceliveness.PassiveFaceLivenessActivity;
import com.combateafraude.passivefaceliveness.input.PassiveFaceLiveness;
import com.combateafraude.passivefaceliveness.output.PassiveFaceLivenessResult;

public class MainActivity extends AppCompatActivity {

    private static final String MOBILE_TOKEN = "INSERT_YOUR_MOBILE_TOKEN";
    // The CAF mobile_token, needed to start the SDKs. To request one, mail to daniel.seitenfus@combateafraude.com

    // The registered CPF used in face authenticator
    private static final String CPF = "INSERT_YOUR_CPF";


    // The default flow to scan a front and a back of CNH
    private static final DocumentDetectorStep[] CNH_FLOW = new DocumentDetectorStep[]{
            new DocumentDetectorStep(Document.CNH_FRONT).setStepLabel(R.string.CNH_Front),
            new DocumentDetectorStep(Document.CNH_BACK).setStepLabel(R.string.CNH_Back)
            //You can also set another configuration that can be accessed on: https://docs.combateafraude.com/docs/mobile/android/document-detector/
    };

    // The default flow to scan a front and a back of RG
    private static final DocumentDetectorStep[] RG_FLOW = new DocumentDetectorStep[]{
            new DocumentDetectorStep(Document.RG_FRONT).setStepLabel(R.string.RG_Front),
            new DocumentDetectorStep(Document.RG_BACK).setStepLabel(R.string.RG_Back)
            //You can also set another configuration that can be accessed on: https://docs.combateafraude.com/docs/mobile/android/document-detector/
    };

    // A example of generic flow that scan only one generic document (like OAB, Identidade Militar or RNE) -> To user a GENERIC Document user OTHERS,
    // except RGs and CNHs. You can create whatever DocumentDetectorFlow you want, just need
    // to pass which Document want to be detected in the parameter
    private static final DocumentDetectorStep[] ONE_GENERIC_DOCUMENT_FLOW = new DocumentDetectorStep[]{
            new DocumentDetectorStep(Document.OTHERS).setStepLabel(R.string.OTHER_Document)
            //You can also set another configuration that can be accessed on: https://docs.combateafraude.com/docs/mobile/android/document-detector/
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

        //setting messages to the documentDetector
        MessageSettings messageSettingsDocDetec = new MessageSettings()
                .setVerifyingQualityMessage(R.string.verifyingQualityMessage)
                .setFitTheDocumentMessage(R.string.fitTheDocumentMessage);

        PreviewSettings previewSettings = new PreviewSettings(
                true,
                this.getString(R.string.preview_Title),
                this.getString(R.string.preview_SubTitle),
                this.getString(R.string.preview_Confirm),
                this.getString(R.string.preview_Retry)
        );

        // Create the DocumentDetector parameter
        DocumentDetector documentDetector = new DocumentDetector.Builder(MOBILE_TOKEN)
                .setDocumentSteps(CNH_FLOW) //use the document you want to process
                .setLayout(R.layout.document_detector_template_layout)
                .setMask( R.drawable.document_greenmask,R.drawable.document_whitemask,R.drawable.document_redmask)
                .setMessageSettings(messageSettingsDocDetec)
                .setStyle(R.style.styleCustom)
                .setUseDeveloperMode(true)
                .setUseDebug(true)
                .setUseAdb(true)
                .setUseEmulator(true)
                .setUseRoot(true)
                //Disabling security detections for test purposes. Take a look here: https://docs.caf.io/sdks/android/getting-started/documentdetector#security-validations
                .setPreviewSettings(previewSettings)
                .build();
        // You can also user another configurations, you can see that we offer here: https://docs.caf.io/sdks/android/getting-started/documentdetector


        // Start the DocumentDetectorActivity passing the DocumentDetector object by parameter. The result will be collected in onActivityResult method below
        Intent intent = new Intent(this, DocumentDetectorActivity.class);
        intent.putExtra(DocumentDetector.PARAMETER_NAME, documentDetector);
        startActivityForResult(intent, DOCUMENT_DETECTOR_CODE);
    }



    /**
     * The PassiveFaceLiveness SDK can replace any selfie capture, with anti spoofing advantage and real selfie capture
     */
    public void passiveFaceLiveness(View view) {

        com.combateafraude.passivefaceliveness.input.PreviewSettings previewSettings = new com.combateafraude.passivefaceliveness.input.PreviewSettings(
                true,
                this.getString(R.string.preview_Title),
                this.getString(R.string.preview_SubTitle),
                this.getString(R.string.preview_Confirm),
                this.getString(R.string.preview_Retry)
        );


        //PassiveFaceLiveness Messages
        com.combateafraude.passivefaceliveness.input.MessageSettings messageSettingsPassive = new com.combateafraude.passivefaceliveness.input.MessageSettings()
                .setStepName(R.string.pfl_StepName)
                .setFaceTooFarMessage(R.string.faceTooFarMessage)
                .setFaceTooCloseMessage(R.string.faceTooCloseMessage);


        // Create the PassiveFaceLiveness parameter
        PassiveFaceLiveness passiveFaceLiveness = new PassiveFaceLiveness.Builder(MOBILE_TOKEN)
                .setLayout(R.layout.passive_face_liveness_template_layout)
                .setMask(R.drawable.face_greenmask,R.drawable.face_whitemask,R.drawable.face_redmask)
                .setPreviewSettings(previewSettings)
                .setUseDeveloperMode(true)
                .setUseDebug(true)
                .setUseAdb(true)
                .setUseEmulator(true)
                .setUseRoot(true)
                //Disabling security detections for test purposes. Take a look here: https://docs.caf.io/sdks/android/getting-started/passivefaceliveness#_e1ds1ilttmda
                .setMessageSettings(messageSettingsPassive)
                .build();
        // You can also user another configurations, you can see that we offer here: https://docs.caf.io/sdks/android/getting-started/passivefaceliveness

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
                .setLayout(R.layout.face_template_layout)
                .setMask(R.drawable.face_greenmask,R.drawable.face_whitemask,R.drawable.face_redmask)
                .setPeopleId(CPF)
                // the CPF that has the registered face in CAF server. To register one, you need to create an execution here: https://docs.combateafraude.com/docs/integracao-api/enviar-documento-analise/
                // You can also user another configurations, you can see that we offer here: https://docs.caf.io/sdks/android/getting-started/faceauthenticator
                .setUseDeveloperMode(true)
                .setUseDebug(true)
                .setUseAdb(true)
                .setUseEmulator(true)
                .setUseRoot(true)
                //Disabling security detections for test purposes. Take a look here: https://docs.caf.io/sdks/android/getting-started/faceauthenticator#_nvbnigm4xtcj
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
            // Discover what SDK had finished (In case of Using different class for which one with won't be necessary)
            switch (requestCode) {
                case DOCUMENT_DETECTOR_CODE:
                    DocumentDetectorResult documentDetectorResult = (DocumentDetectorResult) data.getSerializableExtra(DocumentDetectorResult.PARAMETER_NAME);
                    postDocumentDetector(documentDetectorResult);
                    break;
                case PASSIVE_FACE_LIVENESS_CODE:
                    PassiveFaceLivenessResult passiveFaceLivenessResult = (PassiveFaceLivenessResult) data.getSerializableExtra(PassiveFaceLivenessResult.PARAMETER_NAME);
                    postPassiveFaceLiveness(passiveFaceLivenessResult);
                    break;
                case FACE_AUTHENTICATOR:
                    FaceAuthenticatorResult faceAuthenticatorResult = (FaceAuthenticatorResult) data.getSerializableExtra(FaceAuthenticatorResult.PARAMETER_NAME);
                    postFaceAuthenticator(faceAuthenticatorResult);
                    break;
            }
        } else {
            Toast.makeText(this, "You closed the SDK activity!", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //You also have the reasons for failures that can be accessed on our documentation: https://docs.caf.io/sdks/android/sdks-response
    //Failures that can happen using DocumentDetector's SDK
    private void postDocumentDetector(DocumentDetectorResult documentDetectorResult) {

        if (documentDetectorResult != null) {
            SDKFailure sdkFailure = documentDetectorResult.getSdkFailure();
            if (sdkFailure == null) {
                toastMessages("SDK successfully finished");
            } else if (sdkFailure instanceof InvalidTokenReason) {
                toastMessages("Invalid token");
            } else if (sdkFailure instanceof PermissionReason) {
                toastMessages("One or more permission is missing:" + sdkFailure.getMessage());
            } else if (sdkFailure instanceof AvailabilityReason){
                //the String sdkFailure.getMessage() contains instructions to the user
            } else if (sdkFailure instanceof NetworkReason) {
                toastMessages("You don't have internet or the request exceeded the timeout");
            } else if (sdkFailure instanceof ServerReason) {
                toastMessages("There is some server error. Please, notify us: " + sdkFailure.getMessage());
            } else if (sdkFailure instanceof StorageReason) {
                toastMessages("The SDK couldn't save the image because the device doesn't have enough space");
            } else if (sdkFailure instanceof LibraryReason) {
                toastMessages("One internal library failed to execute: " + sdkFailure.getMessage());
            } else if (sdkFailure instanceof SecurityReason){
                switch (sdkFailure.getMessage()) {
                    case "Error 100":
                        toastMessages("SDK blocking emulated devices.");
                    case "Error 200":
                        toastMessages("Blocking of devices with root privileges by the SDK.");
                    case "Error 300":
                        toastMessages("Blocking of devices with developer mode active.");
                    case "Error 400":
                        toastMessages("Bblocking of devices with Android Debug Bridge enabled.");
                    case "Error 500":
                        toastMessages("Blocking of devices with debug mode enabled.");
                    case "Error 600":
                        toastMessages("Blocking of devices with fraudulent app signatures.");
                }
            }
        }
    }

    //Failures that can happen using PassiveFaceLiveness's SDK
    private void postPassiveFaceLiveness(PassiveFaceLivenessResult passiveFaceLivenessResult) {

        if (passiveFaceLivenessResult != null) {
            com.combateafraude.passivefaceliveness.output.failure.SDKFailure sdkFailure;
            sdkFailure = passiveFaceLivenessResult.getSdkFailure();
            if (sdkFailure == null) {
                toastMessages("SDK successfully finished");
            } else if (sdkFailure instanceof com.combateafraude.passivefaceliveness.output.failure.InvalidTokenReason) {
                toastMessages("Invalid token");
            } else if (sdkFailure instanceof com.combateafraude.passivefaceliveness.output.failure.PermissionReason) {
                toastMessages("One or more permission is missing:" + sdkFailure.getMessage());
            } else if (sdkFailure instanceof com.combateafraude.passivefaceliveness.output.failure.ProxyReason){
                //the String sdkFailure.getMessage() contains instructions to the user
            } else if (sdkFailure instanceof com.combateafraude.passivefaceliveness.output.failure.NetworkReason) {
                toastMessages("You don't have internet or the request exceeded the timeout");
            } else if (sdkFailure instanceof com.combateafraude.passivefaceliveness.output.failure.ServerReason) {
                toastMessages("There is some server error. Please, notify us: " + sdkFailure.getMessage());
            } else if (sdkFailure instanceof com.combateafraude.passivefaceliveness.output.failure.StorageReason) {
                toastMessages("The SDK couldn't save the image because the device doesn't have enough space");
            } else if (sdkFailure instanceof com.combateafraude.passivefaceliveness.output.failure.LibraryReason) {
                toastMessages("One internal library failed to execute: " + sdkFailure.getMessage());
            } else if (sdkFailure instanceof com.combateafraude.passivefaceliveness.output.failure.SecurityReason){
                switch (sdkFailure.getMessage()) {
                    case "Error 100":
                        toastMessages("SDK blocking emulated devices.");
                    case "Error 200":
                        toastMessages("Blocking of devices with root privileges by the SDK.");
                    case "Error 300":
                        toastMessages("Blocking of devices with developer mode active.");
                    case "Error 400":
                        toastMessages("Bblocking of devices with Android Debug Bridge enabled.");
                    case "Error 500":
                        toastMessages("Blocking of devices with debug mode enabled.");
                    case "Error 600":
                        toastMessages("Blocking of devices with fraudulent app signatures.");
                }
            }
        }
    }

    //Failures that can happen using FaceAuthenticator's SDK
    private void postFaceAuthenticator(FaceAuthenticatorResult faceAuthenticatorResult) {

        if (faceAuthenticatorResult != null) {

            com.combateafraude.faceauthenticator.output.failure.SDKFailure sdkFailure = faceAuthenticatorResult.getSdkFailure();
            if (sdkFailure == null) {
                toastMessages("SDK successfully finished");
            } else if (sdkFailure instanceof com.combateafraude.faceauthenticator.output.failure.InvalidTokenReason) {
                toastMessages("Invalid token");
            } else if (sdkFailure instanceof com.combateafraude.faceauthenticator.output.failure.PermissionReason) {
                toastMessages("One or more permission is missing:" + sdkFailure.getMessage());
            } else if (sdkFailure instanceof com.combateafraude.faceauthenticator.output.failure.InvalidFaceReason){
                //the String sdkFailure.getMessage() contains instructions to the user
            } else if (sdkFailure instanceof com.combateafraude.faceauthenticator.output.failure.NetworkReason) {
                toastMessages("You don't have internet or the request exceeded the timeout");
            } else if (sdkFailure instanceof com.combateafraude.faceauthenticator.output.failure.ServerReason) {
                toastMessages("There is some server error. Please, notify us: " + sdkFailure.getMessage());
            } else if (sdkFailure instanceof com.combateafraude.faceauthenticator.output.failure.StorageReason) {
                toastMessages("The SDK couldn't save the image because the device doesn't have enough space");
            } else if (sdkFailure instanceof com.combateafraude.faceauthenticator.output.failure.LibraryReason) {
                toastMessages("One internal library failed to execute: " + sdkFailure.getMessage());
            } else if (sdkFailure instanceof com.combateafraude.faceauthenticator.output.failure.SecurityReason){
                switch (sdkFailure.getMessage()) {
                    case "Error 100":
                        toastMessages("SDK blocking emulated devices.");
                    case "Error 200":
                        toastMessages("Blocking of devices with root privileges by the SDK.");
                    case "Error 300":
                        toastMessages("Blocking of devices with developer mode active.");
                    case "Error 400":
                        toastMessages("Bblocking of devices with Android Debug Bridge enabled.");
                    case "Error 500":
                        toastMessages("Blocking of devices with debug mode enabled.");
                    case "Error 600":
                        toastMessages("Blocking of devices with fraudulent app signatures.");
                }
            }
        }
    }

    //Method just to show the error messages better
    private void toastMessages (String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}