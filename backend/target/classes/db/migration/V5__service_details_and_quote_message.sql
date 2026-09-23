ALTER TABLE admin_services ADD COLUMN slug VARCHAR(120);
ALTER TABLE admin_services ADD COLUMN long_description TEXT;
ALTER TABLE admin_services ADD COLUMN benefits TEXT;

UPDATE admin_services SET slug='solar-panels', long_description='We specify high-performance modules for your roof, usage profile and local conditions.', benefits='Tier-one quality modules|Optimised roof layout|Performance monitoring' WHERE name='Solar Panels';
UPDATE admin_services SET slug='on-grid', long_description='A connected system that offsets daytime consumption and enables net metering where available.', benefits='Lower monthly bills|Net-metering guidance|Fast return on investment' WHERE name='On-Grid Solar System';
UPDATE admin_services SET slug='off-grid', long_description='Independent solar and storage for sites where reliability matters more than grid access.', benefits='Battery-backed independence|Designed for remote sites|Energy resilience' WHERE name='Off-Grid Solar System';
UPDATE admin_services SET slug='hybrid', long_description='A flexible architecture that prioritises solar, stores energy and keeps essential loads running.', benefits='Smart battery dispatch|Backup for essentials|Future-ready design' WHERE name='Hybrid Solar System';
UPDATE admin_services SET slug='water-pumps', long_description='Reliable pumping systems matched to bore depth, irrigation demand and available sunlight.', benefits='Lower irrigation costs|Robust pump selection|Agricultural expertise' WHERE name='Solar Water Pumps';
UPDATE admin_services SET slug='solar-lights', long_description='Autonomous lighting that delivers dependable illumination with minimal infrastructure.', benefits='Automatic dusk-to-dawn|Low maintenance|Public-space ready' WHERE name='Solar Lights';
UPDATE admin_services SET slug='solar-cctv', long_description='Solar-powered surveillance designed for remote perimeters, farms and construction sites.', benefits='24/7 monitoring|Remote deployment|Expandable systems' WHERE name='Solar CCTV';
UPDATE admin_services SET slug='solar-inverter', long_description='Efficient, monitored conversion that turns every ray into useful, reliable power.', benefits='High conversion efficiency|Real-time visibility|Compatible upgrades' WHERE name='Solar Inverter';
UPDATE admin_services SET slug='installation', long_description='A managed installation journey from engineering and approvals through commissioning.', benefits='Survey to handover|Safety-first installation|Documentation included' WHERE name='Solar Installation';
UPDATE admin_services SET slug='maintenance', long_description='Service plans and responsive support to protect performance throughout the system lifecycle.', benefits='Preventive service|Fast diagnostics|Long-term care' WHERE name='Maintenance & Support';

ALTER TABLE quote ADD COLUMN city VARCHAR(100);
ALTER TABLE quote ADD COLUMN message TEXT;