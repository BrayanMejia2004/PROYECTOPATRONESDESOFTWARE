const PlantillaSeccion = ({ titulo, badge, datos, extractor, transformador, render }) => {
  const raw = extractor(datos);
  const data = transformador ? transformador(raw) : raw;

  return (
    <div className="estadisticas-card">
      <div className="card-header">
        <h3>{titulo}</h3>
        {badge != null && badge > 0 && <span className="badge-warning">{badge}</span>}
      </div>
      <div className="card-body">
        {render(data)}
      </div>
    </div>
  );
};

export default PlantillaSeccion;
