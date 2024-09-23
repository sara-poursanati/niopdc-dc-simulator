package ir.niopdc.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BaseService <R extends ListCrudRepository<T, I>, T, I> {

    private R repository;

    @Autowired
    protected void setRepository(R repository) {
        this.repository = repository;
    }

    public R getRepository() {
        return repository;
    }

    @Transactional
    public T save(T entity) {
        return repository.save(entity);
    }

    @Transactional
    public List<T> saveAll(Iterable<T> entities) {
        return repository.saveAll(entities);
    }

    @Transactional
    public T update(T entity) {
        return repository.save(entity);
    }

    public T findById(I id) {
        Optional<T> optional = repository.findById(id);
        return optional.orElse(null);
    }

    public long count() {
        return repository.count();
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    public List<T> findAllById(Iterable<I> ids) {
        return repository.findAllById(ids);
    }

    public boolean existsById(I id) {
        return repository.existsById(id);
    }

    @Transactional
    public void delete(T entity) {
        repository.delete(entity);
    }

    @Transactional
    public void deleteById(I id) {
        repository.deleteById(id);
    }

    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }

    @Transactional
    public void deleteAll(Iterable<? extends T> entities) {
        repository.deleteAll(entities);
    }

    @Transactional
    public void deleteAllById(Iterable<? extends I> ids) {
        repository.deleteAllById(ids);
    }
}
