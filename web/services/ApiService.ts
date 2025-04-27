export class ApiService {
    static async login(username: string, password: string) {
        const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/v1/auth/signin`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password }),
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || 'Failed to log in');
        }

        return response.json();
    }

    static async register(username: string, email: string, password: string) {
        const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/v1/auth/signup`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, email, password }),
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || 'Failed to register');
        }

        return response.json();
    }
}
