import { useAppSelector } from '../../../hooks/useStore';
import { selectUser } from '../../../store/modules/user';
import { FoodDetailData } from '../../../types';
import CircleGraph from '../../common/circleGraph/CircleGraph';

interface Props {
	foodData: FoodDetailData;
}

function FoodDetailAnalysis({ foodData }: Props) {
	const user = useAppSelector(selectUser);
	const box = `/assets/${user.isDark ? 'dark' : 'light'}/empty-box.svg`;
	const refrigerator = `/assets/${user.isDark ? 'dark' : 'light'}/empty-refrigerator.svg`;
	return (
		<div className="w-full h-auto px-4 mx-auto my-6">
			<div className="w-full mb-4 font-bold text-left ">
				<span className="text-green"> {foodData.subCategory} </span>
				<span className="text-text"> 소비패턴 요약</span>
			</div>
			<div className="flex items-center justify-between mb-4">
				<div className="flex-col w-full h-[109px] text-sm relative">
					<img className="block w-20 h-20 mx-auto mb-2" src="/assets/common/purchase.svg" alt="purchase" />
					<div className="absolute z-10 text-2xl font-black -translate-x-1/2 left-1/2 top-6 text-yellow text-inherit whitespace-nowrap text-stroke">
						{foodData.purchase}일
					</div>
					<div>평균 구매주기</div>
				</div>
				<div className="flex-col w-full h-[109px] text-sm">
					<div className="flex justify-center mb-2">
						<CircleGraph reverse size="sm" percent={foodData.percent / 100} />
					</div>
					<div>폐기율</div>
				</div>
				<div className="flex-col w-full h-[109px] text-sm relative">
					<img className="block w-20 h-20 mx-auto mb-2" src="/assets/common/cycle.svg" alt="cycle" />
					<div className="absolute z-10 text-2xl font-black -translate-x-1/2 left-1/2 top-6 text-blue whitespace-nowrap text-stroke">
						{foodData.cycle}일
					</div>
					<div>평균 소비주기</div>
				</div>
			</div>
			<div className="w-full px-6 pt-4 pb-2 mb-12 text-xs text-left border rounded-lg stroke">
				{foodData.msg &&
					foodData.msg.map((item, index) => {
						if (index !== 2) {
							return <div className="mb-2">{item}</div>;
						}
						return <div className="mb-2 font-bold">{item}</div>;
					})}
				{!foodData.msg && (
					<div className="mb-2 text-center">
						<img className="block w-24 m-auto" src={box} alt="msg" />
						<div className="mt-4 text-sm">분석 결과가 존재하지 않습니다.</div>
					</div>
				)}
			</div>
			<div className="w-full">
				<div className="pl-2 mb-4 text-sm font-bold text-left">자주 구매하는 상품</div>
				<div className="flex flex-col justify-between w-full h-auto">
					{foodData.preferProducts &&
						foodData.preferProducts.map(item => (
							<div
								key={item}
								className="flex items-center rounded-[16px] px-1 justify-between w-full h-[34px] border stroke"
							>
								<div className="ml-3 text-sm">{item}</div>
								<div
									className="flex items-center w-6 h-6 cursor-pointer rounded-xl bg-paleyellow"
									onClick={() => {
										window.location.href = `https://www.coupang.com/np/search?component=&q=${item}&channel=user`;
									}}
								>
									<img className="block w-4 h-4 m-auto" src="/assets/common/cart.svg" alt="cart" />
								</div>
							</div>
						))}
					{!foodData.preferProducts && (
						<div className="w-full px-6 pt-4 pb-4 mb-2 text-xs text-left border rounded-lg stroke">
							<img className="block w-24 m-auto" src={refrigerator} alt="msg" />
							<div className="mt-4 text-sm">아직 구매한 상품이 존재하지 않습니다.</div>
						</div>
					)}
				</div>
			</div>
		</div>
	);
}

export default FoodDetailAnalysis;
