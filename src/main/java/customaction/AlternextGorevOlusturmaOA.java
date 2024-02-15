package customaction;

import com.evam.sdk.outputaction.AbstractOutputAction;
import com.evam.sdk.outputaction.IOMParameter;
import com.evam.sdk.outputaction.OutputActionContext;
import com.evam.sdk.outputaction.model.DesignerMetaParameters;
import com.evam.sdk.outputaction.model.ReturnParameter;
import com.evam.sdk.outputaction.model.ReturnType;
import com.evam.utils.util.property.FileDefinitions;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class AlternextGorevOlusturmaOA extends AbstractOutputAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlternextGorevOlusturmaOA.class);

    //response  parameters
    private static final String REQUEST_BODY= "REQUEST_BODY";
    private static final String RESPONSE_BODY = "RESPONSE_BODY";
    private static final String STATUS_CODE = "STATUS_CODE";

    private static final String RESPONSE_ERROR = "ERROR";
    private static final String RESPONSE_VALIDATION_OUTPUT= "VALIDATION_OUTPUT";
    private static final String RESPONSE_CRM_RESULT_TYPE = "RESPONSE_CRM_RESULT_TYPE";
    private static final String RESPONSE_RECONCIALTION_ID = "RESPONSE_RECONCIALTION_ID";
    private static final String RESPONSE_IS_SUCCESS = "RESPONSE_IS_SUCCESS";
    private static final String RESPONSE_HAS_ERROR = "HAS_ERROR";
    private static final String RESPONSE_MESSAGE = "MESSAGE";
    private static final String RESPONSE_ERROR_CODE = "ERROR_CODE";
    private static final String RESPONSE_REF_NO = "RESPONSE_REF_NO";
    private static final String RESPONSE_HIDE_RECEIPT = "HIDE_RECEIPT";
    private static final String RESPONSE_TRANSACTION_MESSAGE = "TRANSACTION_MESSAGE";

    //conf file parameter keys
    private static final String API_URL = "GO.request_missionList_api.url";
    private static final String TXN_NAME = "GO.request_missionList_api.txn.name";
    private static final String CLIENT_IP = "GO.request_missionList_api.client.ip";

    //desginer parameters

    private static final String TYPE = "type";
    private static final String TRANSACTION_NAME = "TransactionName";
    private static final String CLIENT_IP_ADDRESS= "ClientIPAddress";
    private static final String CULTURE= "Culture";
    private static final String REQUEST_TYPE_NAME= "RequestTypeName";
    private static final String APPLICATION_TOKEN= "ApplicationToken";
    private static final String SCHEDULED_END="ScheduledEnd";
    private static final String DESCRIPTION="Description";
    private static final String SUBJECT="Subject";
    private static final String TYPE_OF_CUSTOMER="TypeOfCustomer";
    private static final String VRP_ACTIVITY_TYPE="VrpActivityType";
    private static final String CUSTOMER_NO="CustomerNo";
    private static final String DPY_REGISTER_NO="DPYRegisterNo";
    private static final String LIST_NAME_ID="ListNameId";
    private static final String ACTIVITY_TYPE_ID="ActivityTypeId";

    private static final String MESSAGE="Message";
    private static final String DATA="data";


    //The following parameters are taken from the properties file

    private static String apiUrl;
    private static String TxnName;
    private static String ClientIP;



    @Override
    public synchronized void init() {
        Properties properties = new Properties();
        final String configurationFileName = FileDefinitions.CONF_FOLDER + ActionProperties.ALTERNATIFBANK_CONF_FILE;
        try (FileInputStream fileInputStream = new FileInputStream(configurationFileName)) {
            properties.load(fileInputStream);

            apiUrl = properties.getProperty(API_URL);
            if ((apiUrl == null) || (apiUrl.isEmpty())) {
                LOGGER.warn("AlternextGorevOlusturmaOA : API_URL dosya da set edilmemiş. " + configurationFileName);
            }
            TxnName = properties.getProperty(TXN_NAME);
            if ((TxnName == null) || (TxnName.isEmpty())) {
                LOGGER.warn("AlternextGorevOlusturmaOA : API_TXN_NAME dosya da set edilmemiş. " + configurationFileName);
            }
            ClientIP = properties.getProperty(CLIENT_IP);
            if ((ClientIP == null) || (ClientIP.isEmpty())) {
                LOGGER.warn("AlternextGorevOlusturmaOA : API_CLIENT_IP dosya da set edilmemiş. " + configurationFileName);
            }

        } catch (Exception e) {
            LOGGER.error("AlternextGorevOlusturmaOA : ERROR {} ", e.toString());
        }
    }

    @Override
    public int execute(OutputActionContext outputActionContext)  {

        Duration timeElapsed = null;
        Instant startAction = Instant.now();
        String actorId = outputActionContext.getActorId();
        String scenarioName = outputActionContext.getScenarioName();

        String type = (String) outputActionContext.getParameter(TYPE);
        String transactionName = (String) outputActionContext.getParameter(TRANSACTION_NAME);
        String clientIPAddress = (String) outputActionContext.getParameter(CLIENT_IP_ADDRESS);
        String culture = (String) outputActionContext.getParameter(CULTURE);
        String requestTypeName = (String) outputActionContext.getParameter(REQUEST_TYPE_NAME);
        String applicationToken = (String) outputActionContext.getParameter(APPLICATION_TOKEN);
        String scheduledEnd = (String) outputActionContext.getParameter(SCHEDULED_END);
        String description = (String) outputActionContext.getParameter(DESCRIPTION);
        String subject = (String) outputActionContext.getParameter(SUBJECT);
        String typeOfCustomer = (String) outputActionContext.getParameter(TYPE_OF_CUSTOMER);
        String vrpActivityType = (String) outputActionContext.getParameter(VRP_ACTIVITY_TYPE);
        String customerNo = (String) outputActionContext.getParameter(CUSTOMER_NO);
        String dpyRegisterNo = (String) outputActionContext.getParameter(DPY_REGISTER_NO);
        String listNameId = (String) outputActionContext.getParameter(LIST_NAME_ID);
        String activityTypeId = (String) outputActionContext.getParameter(ACTIVITY_TYPE_ID);

        try {
            StringBuilder responseData = new StringBuilder();

            JSONObject soapRequestJson = new JSONObject();
            soapRequestJson.put(TYPE, type);
            soapRequestJson.put(TRANSACTION_NAME, transactionName);
            soapRequestJson.put(CLIENT_IP_ADDRESS, clientIPAddress);
            soapRequestJson.put(CULTURE,culture);

            JSONObject dataJson = new JSONObject();
            dataJson.put(REQUEST_TYPE_NAME, requestTypeName);
            dataJson.put(APPLICATION_TOKEN,applicationToken);

            JSONObject messageJson = new JSONObject();
            messageJson.put(SCHEDULED_END, scheduledEnd);
            messageJson.put(DESCRIPTION, description);
            messageJson.put(SUBJECT, subject);
            messageJson.put(TYPE_OF_CUSTOMER, typeOfCustomer);
            messageJson.put(VRP_ACTIVITY_TYPE, vrpActivityType);
            messageJson.put(CUSTOMER_NO, customerNo);
            messageJson.put(DPY_REGISTER_NO, dpyRegisterNo);
            messageJson.put(LIST_NAME_ID, listNameId);
            messageJson.put(ACTIVITY_TYPE_ID, activityTypeId);

            dataJson.put(MESSAGE, messageJson);
            soapRequestJson.put(DATA, dataJson);

            String request_body = soapRequestJson.toString();
            LOGGER.info("AlternextGorevOlusturmaOA : RequestBody {}", request_body);

            StringEntity entity = new StringEntity(request_body, "UTF-8");

            HttpPost httpPost = new HttpPost(apiUrl);

            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("TxnName", TxnName);
            httpPost.setHeader("ClientIP", ClientIP);

            httpPost.setEntity(entity);
            CloseableHttpClient httpclient = HttpClients.createDefault();

            Instant httpRequestStart = Instant.now();
            CloseableHttpResponse httpResponse = httpclient.execute(httpPost);
            Instant httpRequestStop = Instant.now();

            timeElapsed = Duration.between(httpRequestStart, httpRequestStop);

            LOGGER.info("AlternextGorevOlusturmaOA : Http Request Time Length {} ", timeElapsed.toMillis());

            Instant httpResponseStart = Instant.now();
            Scanner scanner = new Scanner(httpResponse.getEntity().getContent());
            while (scanner.hasNext()) {
                responseData.append(scanner.nextLine().trim());
            }
            scanner.close();
            Instant httpResponseStop = Instant.now();
            timeElapsed = Duration.between(httpResponseStart, httpResponseStop);

            LOGGER.info("AlternextGorevOlusturmaOA : Http Request Time Length {} ", timeElapsed.toMillis());

            String response_body = responseData.toString();


            outputActionContext.getReturnMap().put(REQUEST_BODY, request_body);
            outputActionContext.getReturnMap().put(RESPONSE_BODY, response_body);


                JSONObject parseString = new JSONObject(response_body);

                JSONObject dataObject = parseString.getJSONObject(DATA);
                JSONObject operationResultObject = dataObject.getJSONObject("operationResult");

                int status = parseString.getInt("status");
                String error = parseString.optString("error");
                String validationOutput = parseString.optString("validationOutput");

                boolean hasError = operationResultObject.getBoolean("hasError");
                String message = operationResultObject.getString("message");
                String errorCode = operationResultObject.getString("errorCode");

                JSONObject responseObject = dataObject.getJSONObject("response");
                String crmResultType = responseObject.getString("$type");

                JSONObject crmResult = responseObject.getJSONObject("CrmResult");
                String reconciliationId = crmResult.getString("ReconciliationId");
                boolean isSuccess = crmResult.getBoolean("IsSuccess");

                int responseRefNo = responseObject.getInt("ResponseRefNo");
                boolean hideReceipt = responseObject.getBoolean("HideReceipt");
                String transactionMessage = responseObject.getString("TransactionMessage");

                LOGGER.info("AlternextGorevOlusturmaOA : SCENARIO_NAME {},ACTOR_ID {}, REQUEST_BODY {},STATUS_CODE {},RESPONSE_BODY {},", scenarioName, actorId, request_body, status,response_body);

                outputActionContext.getReturnMap().put(STATUS_CODE, status);
                outputActionContext.getReturnMap().put(RESPONSE_ERROR, error);
                outputActionContext.getReturnMap().put(RESPONSE_VALIDATION_OUTPUT,validationOutput );
                outputActionContext.getReturnMap().put(RESPONSE_HAS_ERROR, hasError);
                outputActionContext.getReturnMap().put(RESPONSE_MESSAGE, message);
                outputActionContext.getReturnMap().put(RESPONSE_ERROR_CODE, errorCode);
                outputActionContext.getReturnMap().put(RESPONSE_CRM_RESULT_TYPE, crmResultType);
                outputActionContext.getReturnMap().put(RESPONSE_RECONCIALTION_ID, reconciliationId);
                outputActionContext.getReturnMap().put(RESPONSE_IS_SUCCESS,isSuccess);
                outputActionContext.getReturnMap().put(RESPONSE_REF_NO,responseRefNo);
                outputActionContext.getReturnMap().put(RESPONSE_HIDE_RECEIPT,hideReceipt);
                outputActionContext.getReturnMap().put(RESPONSE_TRANSACTION_MESSAGE,transactionMessage);



        } catch (Exception e) {
            LOGGER.error("AlternextGorevOlusturmaOA : Error {}", e.toString());
            return -1;
        }

        Instant stopAction = Instant.now();
        timeElapsed = Duration.between(startAction, stopAction);
        LOGGER.info("AlternextGorevOlusturmaOA : Action Time Length {} ", timeElapsed.toMillis());
        return 0;
    }
    @Override
    protected List<IOMParameter> getParameters() {
        ArrayList<IOMParameter> parameters = new ArrayList<>();
        parameters.add(new IOMParameter(TYPE, "type"));
        parameters.add(new IOMParameter(TRANSACTION_NAME, "Transaction_Name"));
        parameters.add(new IOMParameter(CLIENT_IP_ADDRESS, "Client_IP_Address"));
        parameters.add(new IOMParameter(CULTURE, "Culture"));
        parameters.add(new IOMParameter(REQUEST_TYPE_NAME, "Request_Type_Name"));
        parameters.add(new IOMParameter(APPLICATION_TOKEN, "Application_Token"));
        parameters.add(new IOMParameter(SCHEDULED_END, "Scheduled_End"));
        parameters.add(new IOMParameter(DESCRIPTION, "Description"));
        parameters.add(new IOMParameter(SUBJECT, "Subject"));
        parameters.add(new IOMParameter(TYPE_OF_CUSTOMER, "Type_Of-Customer"));
        parameters.add(new IOMParameter(VRP_ACTIVITY_TYPE, "Vrp_Activity_Type"));
        parameters.add(new IOMParameter(CUSTOMER_NO, "Customer_No"));
        parameters.add(new IOMParameter(DPY_REGISTER_NO, "DPY_Register_No"));
        parameters.add(new IOMParameter(LIST_NAME_ID, "List_Name_Id"));
        parameters.add(new IOMParameter(ACTIVITY_TYPE_ID, "Activity_Type_Id"));


        return parameters;
    }

    @Override
    public boolean actionInputStringShouldBeEvaluated() {
        return false;
    }

    @Override
    public String getVersion() {
        return "v1.0.0";
    }

    @Override
    public ReturnParameter[] getRetParams(DesignerMetaParameters designerMetaParameters) {
        ReturnParameter request_body= new ReturnParameter(REQUEST_BODY, ReturnType.String);
        ReturnParameter response = new ReturnParameter(RESPONSE_BODY, ReturnType.String);

        ReturnParameter statusCode = new ReturnParameter(STATUS_CODE, ReturnType.Numeric);
        ReturnParameter error = new ReturnParameter(RESPONSE_ERROR,ReturnType.String);
        ReturnParameter validation_output = new ReturnParameter(RESPONSE_VALIDATION_OUTPUT,ReturnType.String);
        ReturnParameter response_crm_result_type = new ReturnParameter(RESPONSE_CRM_RESULT_TYPE, ReturnType.String);
        ReturnParameter response_reconcilation_id = new ReturnParameter(RESPONSE_RECONCIALTION_ID, ReturnType.String);
        ReturnParameter response_is_success = new ReturnParameter(RESPONSE_IS_SUCCESS, ReturnType.String);
        ReturnParameter operationResultHasError = new ReturnParameter(RESPONSE_HAS_ERROR, ReturnType.String);
        ReturnParameter operationResultMessage = new ReturnParameter(RESPONSE_MESSAGE, ReturnType.String);
        ReturnParameter operationResultErrorCode = new ReturnParameter(RESPONSE_ERROR_CODE, ReturnType.String);
        ReturnParameter responseRefNo = new ReturnParameter(RESPONSE_REF_NO, ReturnType.Numeric);
        ReturnParameter hideReceipt = new ReturnParameter(RESPONSE_HIDE_RECEIPT, ReturnType.String);
        ReturnParameter transactionMessage = new ReturnParameter(RESPONSE_TRANSACTION_MESSAGE, ReturnType.String);



        return new ReturnParameter[]{request_body,response, statusCode,error,validation_output,response_crm_result_type,response_reconcilation_id,response_is_success, operationResultHasError ,operationResultMessage, operationResultErrorCode, responseRefNo ,hideReceipt ,transactionMessage};
    }

    @Override
    public boolean isReturnable() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Bu aksiyon alternextte görev oluşturmak için triggerlanır.";
    }
}