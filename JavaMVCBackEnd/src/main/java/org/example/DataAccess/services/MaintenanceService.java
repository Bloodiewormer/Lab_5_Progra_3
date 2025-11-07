package org.example.DataAccess.services;

import org.example.Domain.models.Car;
import org.example.Domain.models.Maintenance;
import org.example.Domain.models.MaintenanceType;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public class MaintenanceService {
    private final SessionFactory sessionFactory;

    public MaintenanceService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // -------------------------
    // CREATE
    // -------------------------
    public Maintenance createMaintenance(String description, MaintenanceType type, LocalDateTime maintenanceDate, Long carId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            Car car = session.find(Car.class, carId);
            if (car == null) {
                throw new RuntimeException("Car not found with id: " + carId);
            }

            Maintenance maintenance = new Maintenance();
            maintenance.setDescription(description);
            maintenance.setType(type);
            maintenance.setMaintenanceDate(maintenanceDate);
            maintenance.setCarMaintenance(car);

            session.persist(maintenance);
            tx.commit();

            // Initialize the car relationship
            Hibernate.initialize(maintenance.getCarMaintenance());

            return maintenance;
        } catch (Exception e) {
            String message = String.format("An error occurred when processing: %s. Details: %s", "createMaintenance", e);
            System.out.println(message);
            throw e;
        }
    }

    // -------------------------
    // READ
    // -------------------------
    public Maintenance getMaintenanceById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Maintenance maintenance = session.find(Maintenance.class, id);
            if (maintenance != null) {
                Hibernate.initialize(maintenance.getCarMaintenance());
            }
            return maintenance;
        } catch (Exception e) {
            String message = String.format("An error occurred when processing: %s. Details: %s", "getMaintenanceById", e);
            System.out.println(message);
            throw e;
        }
    }

    public List<Maintenance> getAllMaintenanceByCarId(Long carId) {
        try (Session session = sessionFactory.openSession()) {
            List<Maintenance> maintenances = session.createQuery(
                            "FROM Maintenance m WHERE m.carMaintenance.id = :carId", Maintenance.class)
                    .setParameter("carId", carId)
                    .list();

            // Initialize relationships
            maintenances.forEach(m -> Hibernate.initialize(m.getCarMaintenance()));

            return maintenances;
        } catch (Exception e) {
            String message = String.format("An error occurred when processing: %s. Details: %s", "getAllMaintenanceByCarId", e);
            System.out.println(message);
            throw e;
        }
    }

    // -------------------------
    // UPDATE
    // -------------------------
    public Maintenance updateMaintenance(Long maintenanceId, String description, MaintenanceType type, LocalDateTime maintenanceDate) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            Maintenance maintenance = session.find(Maintenance.class, maintenanceId);
            if (maintenance != null) {
                maintenance.setDescription(description);
                maintenance.setType(type);
                maintenance.setMaintenanceDate(maintenanceDate);
                session.merge(maintenance);

                // Initialize relationships
                Hibernate.initialize(maintenance.getCarMaintenance());
            }

            tx.commit();
            return maintenance;
        } catch (Exception e) {
            String message = String.format("An error occurred when processing: %s. Details: %s", "updateMaintenance", e);
            System.out.println(message);
            throw e;
        }
    }

    // -------------------------
    // DELETE
    // -------------------------
    public boolean deleteMaintenance(Long maintenanceId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            Maintenance maintenance = session.find(Maintenance.class, maintenanceId);
            if (maintenance != null) {
                session.remove(maintenance);
                tx.commit();
                return true;
            }

            tx.rollback();
            return false;
        } catch (Exception e) {
            String message = String.format("An error occurred when processing: %s. Details: %s", "deleteMaintenance", e);
            System.out.println(message);
            throw e;
        }
    }
}