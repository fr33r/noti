package infrastructure;

class DataMapper {

  private final SQLUnitOfWork unitOfWork;

  DataMapper(SQLUnitOfWork unitOfWork) {
    this.unitOfWork = unitOfWork;
  }

  SQLUnitOfWork getUnitOfWork() {
    return this.unitOfWork;
  }
}
