import { describe, expect, it } from "vitest";
import { loginAdmin } from "./adminAuth";

describe("loginAdmin", () => {
  it("accepts the local mock administrator credentials", async () => {
    await expect(loginAdmin({
      email: "admin@solarindustries.local",
      password: "SolarAdmin2026!",
    })).resolves.toMatchObject({
      token: "mock-admin-token",
      user: { role: "ADMIN" },
    });
  });

  it("rejects invalid mock credentials", async () => {
    await expect(loginAdmin({
      email: "admin@solarindustries.local",
      password: "wrong-password",
    })).rejects.toThrow("Invalid admin credentials.");
  });
});
