export function getProtocol() {
    return import.meta.env.NODE_ENV === 'production' ? 'https' : 'http';
}