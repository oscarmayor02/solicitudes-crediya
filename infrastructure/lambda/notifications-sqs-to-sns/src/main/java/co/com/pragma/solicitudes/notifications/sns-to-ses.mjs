// sns-to-ses.mjs
import { SESClient, SendEmailCommand } from "@aws-sdk/client-ses";

/**
 * Cliente SES para enviar emails dinámicos.
 * Asegúrate de verificar dominio/remitente y, si estás en sandbox, los destinatarios.
 */
const ses = new SESClient({ region: process.env.AWS_REGION });
const MAIL_FROM = process.env.MAIL_FROM; // remitente verificado en SES

export const handler = async (event) => {
    for (const rec of event.Records) {
        const notif = JSON.parse(rec.Sns.Message); // evento reenviado por SNS
        const to = notif.email; // destinatario dinámico

        const subject = `Solicitud ${notif.idApplication} ${notif.decision}`;
        const text = `Hola,\n\nTu solicitud #${notif.idApplication} fue ${notif.decision}.\n${notif.observations ? "Observaciones: " + notif.observations : ""}\n\nGracias.`;

        await ses.send(new SendEmailCommand({
            Destination: { ToAddresses: [to] },
            Message: {
                Subject: { Data: subject },
                Body: { Text: { Data: text } }
            },
            Source: MAIL_FROM
        }));
    }
    return { ok: true };
};
