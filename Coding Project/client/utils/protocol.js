export function getProtocol() {
    return import.meta.env.ENVIRONMENT === 'production' ? 'https' : 'http';
}