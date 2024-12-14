export function getProtocol() {
    return import.meta.env.VITE_ENVIRONMENT === 'production' ? 'https' : 'http';
}