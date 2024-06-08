import {Button, Modal} from "react-bootstrap";
import {useState} from "react";
import style from "../Home.module.scss"
import MultiRangeSlider, {ChangeResult} from "multi-range-slider-react";
import {VOIVODESHIPS} from "./PolandMap.tsx";

interface ExportModalProps {
  isOpen: boolean;
  onClose: () => void;
}

const INDICATORS = ["registrations", "deregistrations", "SO2", "NO2", "PM2.5", "Pb(PM10)", "NOx"];
const FORMATS = ["CSV", "JSON", "XML"];

type ExportFormat = typeof FORMATS[number];
type ExportIndicator = typeof INDICATORS[number];
type ExportVoivodeship = typeof VOIVODESHIPS[number];

function mapIndicatorName(indicator: ExportIndicator): string {
  switch (indicator) {
    case "registrations":
      return "Rejestracje";
    case "deregistrations":
      return "Wyrejestrowania";
  }
  return indicator;
}

export default function ExportModal(props: ExportModalProps) {
  const [format, setFormat] = useState<ExportFormat>("JSON");
  const [selectedIndicators, setSelectedIndicators] = useState<ExportIndicator[]>([...INDICATORS]);
  const [selectedVoivodeships, setSelectedVoivodeships] = useState<ExportVoivodeship[]>([...VOIVODESHIPS]);
  const [range, setRange] = useState<[number, number]>([1900, 2020]);

  function onVoivodeshipClick(voivodeship: ExportVoivodeship) {
    if (selectedVoivodeships.includes(voivodeship)) {
      setSelectedVoivodeships(selectedVoivodeships.filter(v => v !== voivodeship));
    } else {
      setSelectedVoivodeships([...selectedVoivodeships, voivodeship]);
    }
  }

  function onIndicatorClick(indicator: ExportIndicator) {
    if (selectedIndicators.includes(indicator)) {
      setSelectedIndicators(selectedIndicators.filter(i => i !== indicator));
    } else {
      setSelectedIndicators([...selectedIndicators, indicator]);
    }
  }

  function onRangeChange(e: ChangeResult) {
    setRange([e.minValue, e.maxValue]);
  }

  function exportData() {
    // export data based on selected format, indicators and range
    if (!FORMATS.includes(format)) {
      return;
    }

    const indicators = selectedIndicators.length === INDICATORS.length ? "*" : selectedIndicators.join(",");
    const voivodeships = selectedVoivodeships.length === VOIVODESHIPS.length ? "*" : selectedVoivodeships.join(",");
    const url = `http://localhost:5000/export/counts/by-year-and-voivodeships/${format}?indicators=${indicators}&voivodeships=${voivodeships}&startYear=${range[0]}&endYear=${range[1]}`;

    const newWindow = window.open(url, '_blank', 'noopener,noreferrer')
    if (newWindow) newWindow.opener = null
  }

  return (
    <Modal show={props.isOpen} onHide={props.onClose} size={"lg"}>
      <Modal.Header closeButton>
        <Modal.Title>Export danych</Modal.Title>
      </Modal.Header>
      <Modal.Body className={"text-center"}>
        <h5>Wybierz format</h5>
        <div className={"d-flex gap-2 justify-content-center"}>
          {FORMATS.map(f => (
            <Button
              key={f}
              variant={format === f ? "primary" : "outline-primary"}
              onClick={() => setFormat(f as ExportFormat)}
            >
              {f}
            </Button>
          ))}
        </div>

        <h5 className={"mt-4"}>Wybierz pola</h5>
        <div className={"d-flex gap-2 justify-content-center flex-wrap"}>
          {INDICATORS.map(i => (
            <Button
              key={i}
              variant={selectedIndicators.includes(i) ? "primary" : "outline-primary"}
              onClick={() => onIndicatorClick(i as ExportIndicator)}
            >
              {mapIndicatorName(i)}
            </Button>
          ))}
        </div>

        <h5 className={"mt-4"}>Wybierz wojewodztwa</h5>
        <div className={"d-flex gap-2 justify-content-center flex-wrap"}>
          {VOIVODESHIPS.map(i => (
            <Button
              key={i}
              variant={selectedVoivodeships.includes(i) ? "primary" : "outline-primary"}
              onClick={() => onVoivodeshipClick(i as ExportVoivodeship)}
            >
              {i}
            </Button>
          ))}
        </div>

        <h5 className={"mt-4"}>Wybierz przedział lat</h5>
        <div className="col-8 mx-auto">
          <MultiRangeSlider
            onChange={onRangeChange}
            className={style.modalSlider}
            min={1900}
            max={2020}
            minValue={1900}
            maxValue={2020}
            thumbLeftColor={"#fff"}
            ruler={false}
            barInnerColor={"#0d6efd"}
          />
        </div>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="outline-primary" onClick={props.onClose}>
          Anuluj
        </Button>
        <Button variant="primary" onClick={exportData}>
          Exportuj
        </Button>
      </Modal.Footer>
    </Modal>
  )
}