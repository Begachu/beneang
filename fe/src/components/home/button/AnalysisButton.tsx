import { BiChevronRight } from 'react-icons/bi';
import { useNavigate } from 'react-router';

function AnalysisButton() {
	const navigator = useNavigate();
	/** 분석 페이지 이동 클릭 이벤트 */
	const handleAnalysis = () => {
		navigator('/analysis/report');
	};
	return (
		<div
			className="flex items-center justify-between w-auto border-2 cursor-pointer hover:bg-slate-300 hover:dark:bg-slate-500 stroke max-w-88 h-14 component"
			onClick={handleAnalysis}
		>
			<img className="w-8 h-8 ml-6" src="/assets/common/graph.svg" alt="graph" />
			<span className="mr-16 text-base font-bold">나의 소비패턴 분석</span>
			<BiChevronRight className="w-6 h-6 mr-6 text-green" />
		</div>
	);
}

export default AnalysisButton;
