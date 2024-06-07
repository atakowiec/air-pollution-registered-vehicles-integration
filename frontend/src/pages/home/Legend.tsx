import { Accordion, AccordionItem, AccordionItemHeading, AccordionItemButton, AccordionItemPanel } from "react-accessible-accordion";
import "react-accessible-accordion/dist/fancy-example.css";
import { Container } from "react-bootstrap";
import { MainNavbar } from "../../components/MainNavbar";

const elements = [
  {
    name: "NO2",
    description: "Jest to zanieczyszczenie powietrza, które powstaje głównie w wyniku spalania paliwa w silnikach samochodowych oraz w procesach przemysłowych, takich jak produkcja energii elektrycznej i produkcja przemysłowa. Emisje z pojazdów są jednym z głównych źródeł NO2 w obszarach miejskich. Wysokie stężenia NO2 mogą powodować problemy zdrowotne, takie jak podrażnienie dróg oddechowych, pogorszenie astmy i innych chorób układu oddechowego, a także zwiększone ryzyko chorób serca.",
  },
  {
    name: "NOx",
    description: "Obejmuje to zarówno tlenek azotu (NO) jak i dwutlenek azotu (NO2). Podobnie jak NO2, NOx powstaje głównie w wyniku spalania paliwa w silnikach samochodowych i innych procesach przemysłowych. Emisje z pojazdów mogą znacznie przyczyniać się do ogólnej emisji NOx. Tlenki azotu mogą przyczyniać się do formowania smogu, kwasnych deszczy oraz powstawania pyłów atmosferycznych.",
  },
  {
    name: "PM2.5",
    description: "Te bardzo małe cząstki stałe lub ciekłe zawieszone w powietrzu mogą pochodzić z różnych źródeł, w tym z samochodów, przemysłu, spalania drewna i innych procesów spalania. W przypadku samochodów, silniki spalinowe, zwłaszcza te z silnikiem diesla, są głównym źródłem emisji PM2.5. Te cząstki są szczególnie niebezpieczne dla zdrowia, ponieważ mogą przenikać głęboko do płuc i krwiobiegu, powodując szereg problemów zdrowotnych, od podrażnienia dróg oddechowych po choroby serca i nowotwory płuc.",
  },
  {
    name: "Pb(PM10)",
    description: "Ołów może być obecny w pyłach zawieszonych (PM10) pochodzących z emisji spalin samochodowych, przemysłowych i innych źródeł zanieczyszczeń. W przeszłości dodawano ołów do benzyny jako środek przeciwstukowy, co było głównym źródłem emisji ołowiu z pojazdów. Choć ołów nie jest już używany w benzynie w większości krajów, nadal może być obecny w atmosferze z powodu recyrkulacji wcześniejszych emisji. Ekspozycja na ołów może prowadzić do uszkodzenia układu nerwowego, upośledzenia funkcji poznawczych i problemów z rozwojem u dzieci.",
  },
  {
    name: "SO2",
    description: "Ten gazowy zanieczyszczacz powietrza może pochodzić z emisji pojazdów z silnikami spalinowymi, zwłaszcza tych zasilanych paliwami zawierającymi siarkę, takimi jak nieoczyszczone oleje napędowe. Podczas spalania siarka w paliwie jest przekształcana w SO2. Ten związek chemiczny może powodować podrażnienie dróg oddechowych, pogorszenie astmy i innych chorób układu oddechowego, a także przyczyniać się do powstawania kwasów atmosferycznych i smogu.",
  },
];

export default function Legend() {
  return (
    <>
      <MainNavbar />
      <Container className="bg-light rounded text-center p-4 mt-5 col-12 col-md-8 col-xl-6 col-xxl-5">
        <h3>Legenda Zanieczyszczenia</h3>
        <Accordion allowZeroExpanded>
          {elements.map((element, index) => (
            <AccordionItem key={index}>
              <AccordionItemHeading>
                <AccordionItemButton>{element.name}</AccordionItemButton>
              </AccordionItemHeading>
              <AccordionItemPanel>
                <p>{element.description}</p>
              </AccordionItemPanel>
            </AccordionItem>
          ))}
        </Accordion>
      </Container>
    </>
  );
}
