import { describe, expect, it, vi, beforeEach } from "vitest";

vi.mock("./api", () => ({
  publicService: {
    services: vi.fn(),
    service: vi.fn(),
    settings: vi.fn(),
  },
}));

import { publicService } from "./api";
import { loadService, loadServices, loadSettings, normalizePublicService } from "./catalog";

describe("public catalog integration", () => {
  beforeEach(() => vi.clearAllMocks());

  it("normalizes backend services into the shape the UI expects", () => {
    const view = normalizePublicService({
      slug: "solar-panels",
      name: "Solar Panels",
      description: "desc",
      icon: "✦",
      benefits: "A|B|C",
      imageUrl: "x.png",
    });
    expect(view.slug).toBe("solar-panels");
    expect(view.image).toBe("x.png");
    expect(view.benefits).toEqual(["A", "B", "C"]);
  });

  it("returns live services when the API responds", async () => {
    publicService.services.mockResolvedValue([{ slug: "solar-panels", name: "Solar Panels", benefits: "One|Two" }]);
    const rows = await loadServices();
    expect(rows).toHaveLength(1);
    expect(rows[0].benefits).toEqual(["One", "Two"]);
  });

  it("falls back to the static catalog when the API is unavailable", async () => {
    publicService.services.mockRejectedValue(new Error("offline"));
    const rows = await loadServices();
    expect(rows.length).toBeGreaterThan(0);
    expect(rows[0].slug).toBe("solar-panels");
  });

  it("falls back to the matching static service when a slug lookup fails offline", async () => {
    publicService.service.mockRejectedValue(new Error("offline"));
    await expect(loadService("on-grid")).resolves.toMatchObject({ slug: "on-grid" });
    await expect(loadService("missing-service")).resolves.toBeNull();
  });

  it("returns null when the backend reports the service as missing", async () => {
    publicService.service.mockRejectedValue({ response: { status: 404 } });
    await expect(loadService("on-grid")).resolves.toBeNull();
  });

  it("keeps the supplied settings fallback when the API fails", async () => {
    publicService.settings.mockRejectedValue(new Error("offline"));
    await expect(loadSettings({ phone: "123" })).resolves.toEqual({ phone: "123" });
  });
});