// index.mjs
import { SNSClient, PublishCommand } from "@aws-sdk/client-sns";

/**
 * Cliente SNS usando credenciales y región provistas por el entorno de Lambda.
 */
const sns = new SNSClient({ region: process.env.AWS_REGION });
const TOPIC_ARN = process.env.SNS_TOPIC_ARN;

/**
 * Handler invocado por eventos SQS.
 * Cada record.body es un JSON de ApplicationDecisionEvent.
 */
export const handler = async (event) => {
    for (const record of event.Records) {
        const msg = JSON.parse(record.body); // parsea el payload

        // Construye asunto y cuerpo en texto plano
        const subject = `Solicitud ${msg.idApplication} ${msg.decision}`;
        const text = [
            `Hola,`,
            ``,
            `Tu solicitud #${msg.idApplication} fue ${msg.decision}.`,
            msg.observations ? `Observaciones: ${msg.observations}` : null,
            ``,
            `Gracias.`
        ].filter(Boolean).join("\n");

        // Publica en el tópico SNS (emails suscritos recibirán la notificación)
        await sns.send(new PublishCommand({
            TopicArn: TOPIC_ARN,
            Subject: subject,
            Message: text
        }));
    }
    return { ok: true };
};
