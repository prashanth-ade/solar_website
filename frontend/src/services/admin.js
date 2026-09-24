import { api } from "./api";

const normalizeDate = value => value
  ? new Date(value).toLocaleDateString("en-IN", { day: "2-digit", month: "short", year: "numeric" })
  : "—";

const normalizeQuoteStatus = status => ({
  DRAFT: "Pending",
  PENDING: "Pending",
  CONTACTED: "Contacted",
  APPROVED: "Contacted",
  COMPLETED: "Completed",
}[status] || "Pending");

const normalizeQuote = quote => ({
  id: quote.id,
  customer: quote.name || quote.user?.name || quote.email || "Unknown customer",
  phone: quote.phone || "",
  service: quote.propertyType || "Solar consultation",
  message: quote.message || `${quote.propertyType || "Solar"} request for ${quote.pincode || "site survey"}.`,
  date: normalizeDate(quote.createdAt),
  status: normalizeQuoteStatus(quote.status),
  backendStatus: quote.status || "DRAFT",
});

const normalizeService = service => ({
  ...service,
  image: service.image || service.imageUrl || "",
  status: service.status === "INACTIVE" ? "Inactive" : "Active",
});

const servicePayload = service => ({
  slug: service.slug || "",
  longDescription: service.longDescription || "",
  benefits: Array.isArray(service.benefits) ? service.benefits.join("|") : (service.benefits || ""),
  name: service.name,
  description: service.description,
  imageUrl: service.image || service.imageUrl || "",
  status: (service.status || "Active").toUpperCase(),
  icon: service.icon || "",
  displayOrder: service.order || service.displayOrder || 0,
});

const normalizeCalculatorRequest = request => ({
  ...request,
  customer: request.name || request.email || "Anonymous request",
  phone: request.phone || "—",
  propertyType: request.propertyType || "—",
  monthlyBill: request.monthlyBill || 0,
  recommendedSystem: request.recommendedKw ? `${request.recommendedKw} KW` : "—",
  date: normalizeDate(request.createdAt),
});

export async function getDashboard() {
  const [dashboard, reports] = await Promise.all([
    api.get("/admin/dashboard").then(response => response.data),
    api.get("/admin/reports").then(response => response.data),
  ]);
  return {
    ...dashboard,
    reports,
    totalServices: reports?.totalServices ?? dashboard.totalServices ?? 0,
    totalEnquiries: dashboard.totalQuotes ?? 0,
    calculatorRequests: reports?.calculatorRequests ?? 0,
  };
}

export async function getCustomers() {
  const customers = await api.get("/admin/customers").then(response => response.data);
  return customers.map((customer, index) => ({
    ...customer,
    phone: customer.phone || "",
    location: customer.location || "",
    registeredDate: normalizeDate(customer.registeredDate || customer.createdAt),
    id: customer.id || index,
  }));
}

export async function getCustomer(id) {
  return api.get(`/admin/customers/${id}`).then(response => response.data);
}

export async function getEnquiries() {
  return api.get("/quotes/admin").then(response => response.data.map(normalizeQuote));
}

export async function updateEnquiryStatus(id, status) {
  const backendStatus = { Pending: "PENDING", Contacted: "CONTACTED", Completed: "COMPLETED" }[status] || "PENDING";
  const quote = await api.patch(`/quotes/admin/${id}/status?status=${backendStatus}`).then(response => response.data);
  return normalizeQuote(quote);
}

export async function getServices() {
  return api.get("/admin/services").then(response => response.data.map(normalizeService));
}

export async function getService(id) {
  return api.get(`/admin/services/${id}`).then(response => normalizeService(response.data));
}

export async function uploadServiceImage(file) {
  const form = new FormData();
  form.append("file", file);
  return api.post("/admin/services/upload-image", form, {
    headers: { "Content-Type": false },
  }).then(response => response.data);
}

export async function saveService(service, imageFile) {
  let imageUrl = service.image || service.imageUrl || "";
  if (imageFile) {
    const uploaded = await uploadServiceImage(imageFile);
    imageUrl = uploaded.imageUrl;
  }
  const path = service.id ? `/admin/services/${service.id}` : "/admin/services";
  const method = service.id ? "put" : "post";
  return api[method](path, servicePayload({ ...service, image: imageUrl })).then(response => normalizeService(response.data));
}

export async function deleteService(id) {
  await api.delete(`/admin/services/${id}`);
}

export async function getCalculatorRequests() {
  return api.get("/admin/calculator-requests")
    .then(response => response.data.map(normalizeCalculatorRequest));
}

export async function getSettings() {
  return api.get("/admin/settings").then(response => response.data);
}

export async function saveSettings(settings) {
  return api.put("/admin/settings", settings).then(response => response.data);
}
