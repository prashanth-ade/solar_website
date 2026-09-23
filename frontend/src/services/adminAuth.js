const ADMIN_EMAIL = "admin@solarindustries.local";
const ADMIN_PASSWORD = "SolarAdmin2026!";

function isAdmin(user) {
  return user?.role === "ADMIN" || user?.role === "ROLE_ADMIN";
}

export async function loginAdmin({ email, password }) {
  if (import.meta.env.VITE_ADMIN_AUTH_MODE === "api") {
    const { authService } = await import("./api");
    const response = await authService.login({ email, password });
    if (!isAdmin(response.user)) {
      throw new Error("This account does not have administrator access.");
    }
    return response;
  }

  if (email.trim().toLowerCase() !== ADMIN_EMAIL || password !== ADMIN_PASSWORD) {
    throw new Error("Invalid admin credentials.");
  }

  return {
    token: "mock-admin-token",
    user: { id: "admin-1", name: "Admin", email: ADMIN_EMAIL, role: "ADMIN" },
  };
}
