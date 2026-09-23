import { publicService } from "./api";
import { serviceCatalog } from "../data";

export const normalizePublicService = (service, index = 0) => ({
  slug: service.slug || `service-${index + 1}`,
  name: service.name || "Solar service",
  icon: service.icon || "☼",
  description: service.description || "",
  longDescription: service.longDescription || service.description || "",
  benefits: Array.isArray(service.benefits)
    ? service.benefits
    : typeof service.benefits === "string" && service.benefits
      ? service.benefits.split("|").filter(Boolean)
      : [],
  image: service.image || service.imageUrl || "",
});

const mergeWithCatalog = (service, index) => {
  const normalized = normalizePublicService(service, index);
  const fallback = serviceCatalog.find(item => item.slug === normalized.slug) || serviceCatalog[index];
  return {
    ...(fallback || {}),
    ...normalized,
    benefits: normalized.benefits.length ? normalized.benefits : fallback?.benefits || [],
  };
};

export async function loadServices() {
  try {
    const rows = await publicService.services();
    if (Array.isArray(rows) && rows.length) return rows.map(mergeWithCatalog);
    return serviceCatalog;
  } catch {
    return serviceCatalog;
  }
}

export async function loadService(slug) {
  try {
    const row = await publicService.service(slug);
    return mergeWithCatalog(row, serviceCatalog.findIndex(item => item.slug === slug));
  } catch (error) {
    if (error.response?.status === 404) return null;
    return serviceCatalog.find(item => item.slug === slug) || null;
  }
}

export async function loadSettings(fallback = {}) {
  try {
    return await publicService.settings();
  } catch {
    return fallback;
  }
}